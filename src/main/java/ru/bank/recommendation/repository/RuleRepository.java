package ru.bank.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.recommendation.model.RuleEntity;

public interface RuleRepository extends JpaRepository<RuleEntity, Long> {
}
