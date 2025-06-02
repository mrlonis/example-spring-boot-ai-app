package com.mrlonis.example.ai;

import com.mrlonis.example.ai.test.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {}
}
