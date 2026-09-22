package ru.bank.recommendation.model;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

public class RuleDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize_shouldMapSnakeCaseToCamelCase() throws Exception {
        String json = """
                {
                    "product_name": "Кредитная карта",
                    "product_id": "123e4567-e89b-12d3-a456-426614174000",
                    "product_text": "Текст карты",
                    "rule": []
                }
                """;
        RuleDto ruleDto = objectMapper.readValue(json, RuleDto.class);
        Assertions.assertEquals("Кредитная карта", ruleDto.getProductName());
        Assertions.assertEquals("Текст карты", ruleDto.getProductText());
    }

    @Test
    public void serialize_shouldConvertToSnakeCaseJson() throws Exception {
        RuleDto ruleDto = new RuleDto();
        ruleDto.setProductName("Кредитная карта");
        ruleDto.setProductId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        ruleDto.setProductText("Текст карты");
        ruleDto.setRule(List.of()); // Передаем пустой список, чтобы не было null
        String json = objectMapper.writeValueAsString(ruleDto);

        Assertions.assertTrue(json.contains("\"product_name\""), "Должен быть ключ product_name");
        Assertions.assertTrue(json.contains("\"product_id\""), "Должен быть ключ product_id");
        Assertions.assertTrue(json.contains("\"product_text\""), "Должен быть ключ product_text");

        Assertions.assertFalse(json.contains("productName"), "Не должно быть productName");
    }
}