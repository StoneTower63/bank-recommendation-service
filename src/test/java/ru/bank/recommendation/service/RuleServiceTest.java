package ru.bank.recommendation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bank.recommendation.model.RuleStatsDto;
import ru.bank.recommendation.model.RuleStatsResponse;
import ru.bank.recommendation.repository.RuleStatsRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    @Mock
    private RuleStatsRepository ruleStatsRepository;

    @InjectMocks
    private RuleService ruleService;

    @Test
    @DisplayName("Есть правила со статистикой: возвращается список с правильными rule_id и count")
    void shouldReturnStatsWhenDataExists() {

        RuleStatsDto stat1 = new RuleStatsDto(1L, 5L);
        RuleStatsDto stat2 = new RuleStatsDto(2L, 10L);
        when(ruleStatsRepository.findAllWithStats()).thenReturn(List.of(stat1, stat2));

        RuleStatsResponse response = ruleService.getStats();

        assertThat(response.stats()).hasSize(2);
        assertThat(response.stats()).usingElementComparatorIgnoringFields("ruleId", "count")
                .containsExactlyInAnyOrder(stat1, stat2);

        assertThat(response.stats().get(0).ruleId()).isEqualTo(1L);
        assertThat(response.stats().get(0).count()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Есть правило без статистики: в ответе оно тоже присутствует, но с count = 0")
    void shouldReturnZeroCountWhenStatsMissing() {

        RuleStatsDto statWithZero = new RuleStatsDto(42L, 0L);
        when(ruleStatsRepository.findAllWithStats()).thenReturn(List.of(statWithZero));

        RuleStatsResponse response = ruleService.getStats();

        assertThat(response.stats()).hasSize(1);
        assertThat(response.stats().get(0).ruleId()).isEqualTo(42L);
        assertThat(response.stats().get(0).count()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Правил нет вообще: возвращается пустой список")
    void shouldReturnEmptyListWhenNoRules() {

        when(ruleStatsRepository.findAllWithStats()).thenReturn(List.of());

        RuleStatsResponse response = ruleService.getStats();

        assertThat(response.stats()).isEmpty();
    }
}

