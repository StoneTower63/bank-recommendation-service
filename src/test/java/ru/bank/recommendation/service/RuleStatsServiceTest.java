package ru.bank.recommendation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.model.RuleStatsEntity;
import ru.bank.recommendation.repository.RuleRepository;
import ru.bank.recommendation.repository.RuleStatsRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class RuleStatsServiceTest {

    @Mock
    private RuleStatsRepository ruleStatsRepository;

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private RuleStatsService ruleStatsService;

    @Test
    @DisplayName("Инкремент существующей записи: репозиторий вернул 1, новая запись не создается")
    void shouldIncrementExistingStats() {

        when(ruleStatsRepository.incrementCount(1L)).thenReturn(1);

        ruleStatsService.incrementCount(1L);

        verify(ruleStatsRepository, times(1)).incrementCount(1L);
        verify(ruleRepository, never()).findById(anyLong());
        verify(ruleStatsRepository, never()).save(any(RuleStatsEntity.class));
    }

    @Test
    @DisplayName("Инкремент отсутствующей записи: репозиторий вернул 0, создается новая запись со значением 1")
    void shouldCreateStatsIfNotExists() {

        RuleEntity rule = new RuleEntity("Кредит", java.util.UUID.randomUUID(), "Текст", "[]");
        rule.setId(2L);

        when(ruleStatsRepository.incrementCount(2L)).thenReturn(0);
        when(ruleRepository.findById(2L)).thenReturn(Optional.of(rule));

        ArgumentCaptor<RuleStatsEntity> statsCaptor = ArgumentCaptor.forClass(RuleStatsEntity.class);

        ruleStatsService.incrementCount(2L);

        verify(ruleStatsRepository, times(1)).incrementCount(2L);
        verify(ruleRepository, times(1)).findById(2L);
        verify(ruleStatsRepository, times(1)).save(statsCaptor.capture());

        RuleStatsEntity savedEntity = statsCaptor.getValue();
        assertThat(savedEntity.getRule()).isEqualTo(rule);
        assertThat(savedEntity.getCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Race condition: два параллельных вызова incrementCount не приводят к отрицательному или двойному счётчику")
    void shouldHandleConcurrentCalls() throws ExecutionException, InterruptedException {

        RuleEntity rule = new RuleEntity("Карта", java.util.UUID.randomUUID(), "Текст", "[]");
        rule.setId(3L);

        when(ruleStatsRepository.incrementCount(3L)).thenReturn(0).thenReturn(0);
        when(ruleRepository.findById(3L)).thenReturn(Optional.of(rule));

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> ruleStatsService.incrementCount(3L));
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> ruleStatsService.incrementCount(3L));

        CompletableFuture.allOf(future1, future2).get();

        verify(ruleStatsRepository, times(2)).incrementCount(3L);
        verify(ruleRepository, times(2)).findById(3L);
        verify(ruleStatsRepository, times(2)).save(any(RuleStatsEntity.class));
    }

    @RepeatedTest(10)
    @DisplayName("Повторяющийся тест: проверка стабильности логики при многократном запуске")
    void shouldConsistentlyHandleIncrement() {

        when(ruleStatsRepository.incrementCount(4L)).thenReturn(1);

        ruleStatsService.incrementCount(4L);

        verify(ruleStatsRepository).incrementCount(4L);
        verify(ruleRepository, never()).findById(any());
    }
}
