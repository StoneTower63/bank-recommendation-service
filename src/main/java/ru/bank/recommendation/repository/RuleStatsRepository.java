package ru.bank.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bank.recommendation.model.RuleStatsEntity;

import java.util.Optional;


@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {
    Optional<RuleStatsEntity> findByRuleId(Long ruleId);

    void deleteByRuleId(Long ruleId);
}
