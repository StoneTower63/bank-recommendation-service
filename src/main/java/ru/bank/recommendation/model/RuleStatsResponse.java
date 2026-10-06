package ru.bank.recommendation.model;

import java.util.List;

/**
 * Ответ API со статистикой срабатываний всех правил.
 *
 * @param stats список статистики по правилам
 */
public record RuleStatsResponse(List<RuleStatsDto> stats) {
}