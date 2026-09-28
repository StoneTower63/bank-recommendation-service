package ru.bank.recommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bank.recommendation.repository.RecommendationRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagementController.class)
public class ManagementControllerClearCachesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationRepository recommendationRepository;

    @MockitoBean
    private BuildProperties buildProperties;

    @Test
    public void clearCaches_shouldReturn200AndCallRepositoryOnce() throws Exception {
        mockMvc.perform(post("/management/clear-caches")).andExpect(status().isOk());

        verify(recommendationRepository, times(1)).clearAllCaches();
    }
}