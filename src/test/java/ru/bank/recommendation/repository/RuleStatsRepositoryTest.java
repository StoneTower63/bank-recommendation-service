package ru.bank.recommendation.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.model.RuleStatsDto;
import ru.bank.recommendation.model.RuleStatsEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RuleStatsRepositoryTest {

    @Autowired
    private RuleStatsRepository ruleStatsRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("incrementCount: счётчик растёт от 1 до 2")
    void shouldIncrementCount() {
        RuleEntity rule = new RuleEntity("Тест", UUID.randomUUID(), "Текст", "[]");
        ruleRepository.save(rule);
        testEntityManager.flush();

        RuleStatsEntity stats = new RuleStatsEntity();
        stats.setRule(rule);
        stats.setCount(0L);
        ruleStatsRepository.save(stats);
        testEntityManager.flush();

        ruleStatsRepository.incrementCount(rule.getId());
        ruleStatsRepository.incrementCount(rule.getId());

        testEntityManager.clear();

        RuleStatsEntity updated = ruleStatsRepository.findByRuleId(rule.getId()).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("findAllWithStats: правила с count=0 тоже попадают")
    void shouldReturnAllRulesIncludingZero() {
        RuleEntity rule1 = ruleRepository.save(new RuleEntity("R1", UUID.randomUUID(), "T", "[]"));
        RuleEntity rule2 = ruleRepository.save(new RuleEntity("R2", UUID.randomUUID(), "T", "[]"));
        testEntityManager.flush();

        RuleStatsEntity stats1 = new RuleStatsEntity();
        stats1.setRule(rule1);
        stats1.setCount(5L);
        ruleStatsRepository.save(stats1);
        testEntityManager.flush();

        List<RuleStatsDto> result = ruleStatsRepository.findAllWithStats();

        testEntityManager.clear();

        assertThat(result).hasSize(3);
        assertThat(result).anyMatch(dto -> dto.count() == 5L);
        assertThat(result).anyMatch(dto -> dto.count() == 0L);
    }
}
