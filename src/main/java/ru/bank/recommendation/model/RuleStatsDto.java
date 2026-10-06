package ru.bank.recommendation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO статистики срабатываний одного правила.
 *
 * @param ruleId идентификатор правила
 * @param count  количество срабатываний
 */
public record RuleStatsDto(
        @JsonProperty("rule_id") Long ruleId,
        Long count
) {
}