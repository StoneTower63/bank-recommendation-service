package ru.bank.recommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bank.recommendation.model.RuleDto;
import ru.bank.recommendation.model.RulesResponseDto;
import ru.bank.recommendation.service.RuleService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RuleController.class)
class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RuleService ruleService;

    private RuleDto createTestRuleDto() {
        RuleDto dto = new RuleDto();
        dto.setId(1L);
        dto.setProductName("Тестовый продукт");
        dto.setProductId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        dto.setProductText("Тестовое описание");
        dto.setRule(List.of());
        return dto;
    }

    @Test
    void postRule_shouldReturnCreatedRuleWithId() throws Exception {
        RuleDto responseDto = createTestRuleDto();
        when(ruleService.createRule(any(RuleDto.class))).thenReturn(responseDto);

        String json = """
                {
                    "product_name": "Тестовый продукт",
                    "product_id": "00000000-0000-0000-0000-000000000001",
                    "product_text": "Тестовое описание",
                    "rule": []
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product_name").value("Тестовый продукт"));
    }

    @Test
    void getRule_shouldReturnDataList() throws Exception {
        RulesResponseDto response = new RulesResponseDto(List.of(createTestRuleDto()));
        when(ruleService.getAllRules()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/rule")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].product_name").value("Тестовый продукт"));
    }

    @Test
    void deleteRule_shouldReturnNoContent() throws Exception {
        doNothing().when(ruleService).deleteRule(eq(1L));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/rule/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}