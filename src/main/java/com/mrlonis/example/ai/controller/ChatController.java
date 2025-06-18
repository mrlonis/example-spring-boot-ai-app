package com.mrlonis.example.ai.controller;

import com.mrlonis.example.ai.entity.Summarization;
import com.mrlonis.example.ai.repository.SummarizationRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
class ChatController {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final SummarizationRepository summarizationRepository;

    public ChatController(
            ChatClient.Builder chatClientBuilder,
            EmbeddingModel embeddingModel,
            VectorStore vectorStore,
            SummarizationRepository summarizationRepository,
            JdbcChatMemoryRepository chatMemoryRepository) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
        this.summarizationRepository = summarizationRepository;
    }

    @PostConstruct
    public void init() {
        vectorStore.<JdbcTemplate>getNativeClient().ifPresent(jdbcTemplate -> {
            boolean hasRows = Boolean.TRUE.equals(
                    jdbcTemplate.queryForObject("SELECT EXISTS (SELECT 1 FROM vector_store)", Boolean.class));
            if (!hasRows) {
                log.info("No Documents found in the vector store. Initializing with sample data.");
                vectorStore.add(generateDefaultDocuments());
                log.info("Added Default Documents to the vector store.");
            } else {
                log.info("Found Documents in the vector store.");
            }
        });
    }

    private static List<Document> generateDefaultDocuments() {
        return List.of(
                new Document(
                        "Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!",
                        Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document(
                        "You walk forward facing the past and you turn back toward the future.",
                        Map.of("meta2", "meta2")));
    }

    @GetMapping("/ai")
    public String generation(
            @RequestParam String userInput, @RequestParam(required = false) @Nullable String conversationId) {
        if (conversationId != null) {
            return this.chatClient
                    .prompt()
                    .user(userInput)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .call()
                    .content();
        }
        return this.chatClient.prompt().user(userInput).call().content();
    }

    @GetMapping("/ai/summarize")
    public String summarization(@RequestParam String userInput, @RequestParam boolean saveSummary) {
        var optionalSummarization = this.summarizationRepository.findByUserInput(userInput);

        if (optionalSummarization.isPresent()) {
            log.info("Found existing summarization: {}", optionalSummarization.get());
            return optionalSummarization.get().getSummary();
        }

        var summary = this.chatClient
                .prompt("Summarize the following content:")
                .user(userInput)
                .call()
                .content();
        if (saveSummary && StringUtils.isNotBlank(summary)) {
            var summarization = Summarization.builder()
                    .userInput(userInput)
                    .summary(summary)
                    .build();
            this.summarizationRepository.save(summarization);
            log.info("Saved summarization: {}", summarization);
        } else {
            log.info("Did not save summarization.");
        }
        return summary;
    }

    @GetMapping("/ai/embedding")
    public Map<String, EmbeddingResponse> embed(@RequestParam String message) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

    @GetMapping("/ai/vector")
    public List<Document> searchDocuments(@RequestParam String query) {
        var result = this.vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build());
        log.info("similarity result: {}", result);
        return result;
    }
}
