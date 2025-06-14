package com.mrlonis.example.ai;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mrlonis.example.ai.test.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class ApplicationIT {
    @Test
    void contextLoads() {
        assertTrue(true, "Context loads successfully");
    }
}
