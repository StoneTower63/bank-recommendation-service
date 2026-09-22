package ru.bank.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bank.recommendation.repository.RecommendationRepository;
import ru.bank.recommendation.service.RuleService;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RuleService ruleService;

    @MockitoBean
    private RecommendationRepository recommendationRepository;

    @Test
    public void getRecommendation_ifRightUserId_returnEmptyList() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recommendation/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getRecommendation_ifWrongUserId_returnBadRequest() throws Exception {
        int userId = 123;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recommendation/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
