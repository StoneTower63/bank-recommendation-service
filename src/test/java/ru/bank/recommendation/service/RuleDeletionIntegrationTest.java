package ru.bank.recommendation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.model.RuleStatsEntity;
import ru.bank.recommendation.repository.RuleRepository;
import ru.bank.recommendation.repository.RuleStatsRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RuleDeletionIntegrationTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleStatsRepository ruleStatsRepository;

    @Test
    @DisplayName("При удалении правила статистика удаляется")
    void shouldDeleteStatsWhenRuleDeleted() {
        RuleEntity rule = new RuleEntity("Тест-удаление", UUID.randomUUID(), "Текст", "[]");
        RuleEntity savedRule = ruleRepository.save(rule);
        Long ruleId = savedRule.getId();

        RuleStatsEntity stats = new RuleStatsEntity();
        stats.setRule(savedRule);
        stats.setCount(5L);
        ruleStatsRepository.save(stats);

        assertThat(ruleStatsRepository.findByRuleId(ruleId))
                .as("Статистика должна существовать до удаления")
                .isPresent();

        ruleService.deleteRule(ruleId);

        assertThat(ruleRepository.findById(ruleId))
                .as("Правило должно быть удалено")
                .isEmpty();

        assertThat(ruleStatsRepository.findByRuleId(ruleId))
                .as("Статистика должна быть удалена вместе с правилом")
                .isEmpty();
    }
}
