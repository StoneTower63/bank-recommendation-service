package ru.bank.recommendation.model;

import java.util.Optional;
import java.util.UUID;

/**
 * Контракт правила рекомендации (устаревшая модель).
 *
 * Оставлен для совместимости. В актуальной версии все правила
 * описаны как динамические (см. {@link RuleDto} и {@link QueryDto}).
 */
public interface RecommendationRuleSet {
    Optional<RecommendationDto> check(UUID userId);
}
