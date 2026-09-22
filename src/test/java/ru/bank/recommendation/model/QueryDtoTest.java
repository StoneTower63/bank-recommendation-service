package ru.bank.recommendation.model;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryDtoTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize_shouldMapQueryDtoFields() throws Exception {
        String json = """
                {
                    "query": "spend_amount",
                    "arguments": [1000, "RUB"],
                    "negate": true
                }
                """;
        QueryDto queryDto = objectMapper.readValue(json, QueryDto.class);
        Assertions.assertEquals("spend_amount", queryDto.getQuery());
        Assertions.assertTrue(queryDto.isNegate());

    }
}