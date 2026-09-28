package ru.bank.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bank.recommendation.model.RuleStatsDto;
import ru.bank.recommendation.model.RuleStatsEntity;

import java.util.List;
import java.util.Optional;


@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {
    Optional<RuleStatsEntity> findByRuleId(Long ruleId);

    @Modifying
    @Query("UPDATE RuleStatsEntity r SET r.count = r.count + 1 WHERE r.rule.id = :ruleId")
    int incrementCount(@Param("ruleId") Long ruleId);

    void deleteByRuleId(Long ruleId);

    @Query("""
            SELECT new ru.bank.recommendation.model.RuleStatsDto(
                r.id,
                COALESCE(s.count, 0L)
            )
            FROM RuleEntity r
            LEFT JOIN RuleStatsEntity s ON s.rule.id = r.id
            """)
    List<RuleStatsDto> findAllWithStats();
}
