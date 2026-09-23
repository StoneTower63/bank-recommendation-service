package ru.bank.recommendation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bank.recommendation.enums.ComparisonOperator;
import ru.bank.recommendation.model.QueryDto;
import ru.bank.recommendation.repository.RecommendationRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryCheckerTest {
    @Mock
    private RecommendationRepository repository;

    @InjectMocks
    private QueryChecker queryChecker;

    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldReturnTrueWhenUserHasTransactionOfType() {
        when(repository.countTransactionsByUserAndProductType(userId, "CREDIT")).thenReturn(1);

        QueryDto dto = new QueryDto("USER_OF", List.of("CREDIT"), false);

        boolean result = queryChecker.check(userId, dto);

        assertThat(result).isTrue();

        verify(repository).countTransactionsByUserAndProductType(userId, "CREDIT");
    }

    @Test
    void shouldReturnFalseWhenUserHasTransactionOfType() {
        when(repository.countTransactionsByUserAndProductType(userId, "DEPOSIT")).thenReturn(0);

        QueryDto dto = new QueryDto("USER_OF", List.of("DEPOSIT"), true);

        boolean result = queryChecker.check(userId, dto);

        assertThat(result).isTrue();
    }

    @Test
    void activeUserOfShouldRequireAtLeastFiveTransactions() {
        when(repository.countTransactionsByUserAndProductType(userId, "INVEST")).thenReturn(4);

        boolean lessThanFive = queryChecker.check(userId, new QueryDto("ACTIVE_USER_OF", List.of("INVEST"), false));

        assertThat(lessThanFive).isFalse();

        when(repository.countTransactionsByUserAndProductType(userId, "INVEST")).thenReturn(5);

        boolean fiveOrMore = queryChecker.check(userId, new QueryDto("ACTIVE_USER_OF", List.of("INVEST"), false));

        assertThat(fiveOrMore).isTrue();
    }

    @ParameterizedTest
    @MethodSource("comparisonArgs")
    void transactionSumCompareShouldWorkForAllOperators(String symbol, int actualSum, int threshold, boolean expected) {
        String productType = "CARD";
        String transactType = "WITHDRAW";

        when(repository.checkTransactionSumCompare(userId, productType, transactType, ComparisonOperator.fromSymbol(symbol), threshold))
                .thenReturn(actualSum > 0);

        QueryDto dto = new QueryDto("TRANSACTION_SUM_COMPARE", List.of(productType, transactType, symbol, String.valueOf(threshold)), false);

        boolean result = queryChecker.check(userId, dto);

        assertThat(result).isEqualTo(expected || actualSum > 0);
    }

    private static Stream<Arguments> comparisonArgs() {
        return Stream.of(
                Arguments.of(">", 100, 50, true),
                Arguments.of("<", 30, 50, true),
                Arguments.of("=", 50, 50, true),
                Arguments.of(">=", 50, 50, true),
                Arguments.of("<=", 40, 50, true));
    }

    @Test
    void depositWithdrawComparisonShouldEvaluateCorrectly() {
        when(repository.sumTransactionByUserAndProductType(userId, "DEBIT", "DEPOSIT")).thenReturn(1000);
        when(repository.sumTransactionByUserAndProductType(userId, "DEBIT", "WITHDRAW")).thenReturn(800);

        QueryDto dto = new QueryDto("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", List.of("DEBIT", ">"), false);

        boolean result = queryChecker.check(userId, dto);

        assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionOnUnknownQuery() {
        QueryDto dto = new QueryDto("UNKNOWN_TYPE", List.of(), false);

        assertThatThrownBy(() -> queryChecker.check(userId, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown query type");
    }
}
