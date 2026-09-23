package ru.bank.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bank.recommendation.repository.RuleRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RuleRepository ruleRepository;

    @BeforeEach
    void setUp() {
        ruleRepository.deleteAll();
    }

    @Test
    void createdRuleShouldAppearInRecommendations() throws Exception {
        String ruleJson = """
                {
                    "product_name": "Тестовый продукт",
                    "product_id": "00000000-0000-0000-0000-000000000001",
                    "product_text": "Тестовое описание",
                    "rule": [
                        {"query": "USER_OF", "arguments": ["DEBIT"], "negate": false}
                    ]
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ruleJson))
                .andExpect(status().isOk());

        UUID testUserId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recommendation/" + testUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Тестовый продукт"));
    }
}