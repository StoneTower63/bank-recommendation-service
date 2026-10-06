package ru.bank.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.recommendation.model.RuleEntity;

/**
 * JPA-репозиторий динамических правил рекомендаций.
 *
 * Хранит правила в таблице {@code rules} (PostgreSQL).
 * Предоставляет стандартный CRUD через {@link JpaRepository}.
 */
public interface RuleRepository extends JpaRepository<RuleEntity, Long> {
}
