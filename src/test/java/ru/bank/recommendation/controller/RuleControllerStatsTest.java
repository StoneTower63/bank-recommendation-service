package ru.bank.recommendation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.bank.recommendation.model.RuleStatsDto;
import ru.bank.recommendation.model.RuleStatsResponse;
import ru.bank.recommendation.service.RuleService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RuleController.class)
public class RuleControllerStatsTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private RuleService ruleService;

    @Test
    @DisplayName("GET /rule/stats 200")
    void shouldReturnStatsWithTwoRules() throws Exception {

        RuleStatsDto rule1 = new RuleStatsDto(1L, 5L);
        RuleStatsDto rule2 = new RuleStatsDto(2L, 0L);
        List<RuleStatsDto> stats = List.of(rule1, rule2);

        when(ruleService.getStats()).thenReturn(new RuleStatsResponse(stats));

        mockMvc.perform(get("/rule/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stats").isArray())
                .andExpect(jsonPath("$.stats").isNotEmpty())
                .andExpect(jsonPath("$.stats").value(Matchers.hasSize(2)))//hasSize(2)
                .andExpect(jsonPath("$.stats[0].rule_id").value(1L))
                .andExpect(jsonPath("$.stats[0].count").value(5L))
                .andExpect(jsonPath("$.stats[1].rule_id").value(2L))
                .andExpect(jsonPath("$.stats[1].count").value(0L));
    }

    @Test
    @DisplayName("GET /rule/stats            200                ,                ")
    void shouldReturnEmptyStatsList() throws Exception {

        when(ruleService.getStats()).thenReturn(new RuleStatsResponse(List.of()));

        mockMvc.perform(get("/rule/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stats").isArray())
                .andExpect(jsonPath("$.stats").isEmpty());
    }

}
