package ru.bank.recommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bank.recommendation.repository.RecommendationRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagementController.class)
public class ManagementControllerInfoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationRepository recommendationRepository;

    @MockitoBean
    private BuildProperties buildProperties;

    @Test
    public void getInfo_shouldReturn200AndCorrectJson() throws Exception {
        when(buildProperties.getName()).thenReturn("bank-recommendation-service");
        when(buildProperties.getVersion()).thenReturn("0.0.1-SNAPSHOT");

        mockMvc.perform(get("/management/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bank-recommendation-service"))
                .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"));
    }
}