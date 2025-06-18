package com.mrlonis.example.ai.repository;

import com.mrlonis.example.ai.entity.Summarization;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummarizationRepository extends JpaRepository<Summarization, UUID> {
    Optional<Summarization> findByUserInput(String userInput);
}
