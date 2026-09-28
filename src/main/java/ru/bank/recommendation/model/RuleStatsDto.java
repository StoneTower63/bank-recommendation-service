package ru.bank.recommendation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RuleStatsDto(
        @JsonProperty("rule_id") Long ruleId,
        Long count
) {
}