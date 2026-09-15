package ru.bank.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bank.recommendation.model.RecommendationDto;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRecommendation_ifRightUserId_returnEmptyList() throws Exception {
        UUID userId = UUID.randomUUID();
        List<RecommendationDto> expectedRecommendations = List.of();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/recommendation/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .equals(expectedRecommendations);
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
