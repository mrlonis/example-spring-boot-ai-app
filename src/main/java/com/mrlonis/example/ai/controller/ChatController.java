package com.mrlonis.example.ai.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChatController {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public ChatController(
            ChatClient.Builder chatClientBuilder,
            EmbeddingModel embeddingModel,
            VectorStore vectorStore,
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

        // List<Document> documents = List.of(
        //         new Document(
        //                 "Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!",
        //                 Map.of("meta1", "meta1")),
        //         new Document("The World is Big and Salvation Lurks Around the Corner"),
        //         new Document(
        //                 "You walk forward facing the past and you turn back toward the future.",
        //                 Map.of("meta2", "meta2")));

        // Add the documents to PGVector
        // vectorStore.add(documents);
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

    @GetMapping("/ai/embedding")
    public Map<String, EmbeddingResponse> embed(@RequestParam String message) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

    @GetMapping("/ai/vector")
    public List<Document> searchDocuments(@RequestParam String query) {
        return this.vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build());
    }
}
