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

/**
 * JPA-репозиторий статистики срабатываний динамических правил.
 *
 * Хранит счётчики в таблице {@code rule_stats} (PostgreSQL).
 * Связан с {@link ru.bank.recommendation.model.RuleEntity} через FK.
 */
@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStatsEntity, Long> {

    /**
     * Находит запись статистики по id правила.
     *
     * @param ruleId идентификатор правила
     * @return запись статистики, если существует
     */
    Optional<RuleStatsEntity> findByRuleId(Long ruleId);

    /**
     * Атомарно инкрементирует счётчик срабатываний правила.
     *
     * @param ruleId идентификатор правила
     * @return количество обновлённых строк (1 — если запись существовала, 0 — если нет)
     */
    @Modifying
    @Query("UPDATE RuleStatsEntity r SET r.count = r.count + 1 WHERE r.rule.id = :ruleId")
    int incrementCount(@Param("ruleId") Long ruleId);

    /**
     * Удаляет запись статистики по id правила.
     *
     * Используется при каскадном удалении правила.
     *
     * @param ruleId идентификатор правила
     */
    @Modifying
    @Query("DELETE FROM RuleStatsEntity s WHERE s.rule.id = :ruleId")
    void deleteByRuleId(@Param("ruleId") Long ruleId);

    /**
     * Возвращает статистику по всем правилам, включая те, у которых count = 0.
     *
     * Использует LEFT JOIN между {@code rules} и {@code rule_stats}.
     *
     * @return список DTO со статистикой
     */
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
