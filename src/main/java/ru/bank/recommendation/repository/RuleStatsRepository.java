package ru.bank.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bank.recommendation.model.RuleStatsEntity;

import java.util.Optional;


@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {
    Optional<RuleStatsEntity> findByRuleId(Long ruleId);

    @Modifying
    @Query("UPDATE RuleStatsEntity r SET r.count = r.count + 1 WHERE r.rule.id = :ruleId")
    int incrementCount(@Param("ruleId") Long ruleId);

    void deleteByRuleId(Long ruleId);
}
