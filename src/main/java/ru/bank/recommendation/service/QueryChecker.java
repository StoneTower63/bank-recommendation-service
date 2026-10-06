package ru.bank.recommendation.service;

import org.springframework.stereotype.Service;
import ru.bank.recommendation.enums.ComparisonOperator;
import ru.bank.recommendation.model.QueryDto;
import ru.bank.recommendation.repository.RecommendationRepository;

import java.util.UUID;

/**
 * Проверяет условия динамического правила для конкретного пользователя.
 *
 * Поддерживает 4 типа запросов:
 * <ul>
 *     <li>USER_OF — есть ли у клиента хотя бы одна транзакция по продуктам указанного типа;</li>
 *     <li>ACTIVE_USER_OF — есть ли 5 и более транзакций;</li>
 *     <li>TRANSACTION_SUM_COMPARE — сравнение суммы транзакций с константой;</li>
 *     <li>TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW — сравнение сумм пополнений и трат.</li>
 * </ul>
 *
 * Поддерживает инверсию результата через флаг {@code negate}.
 */
@Service
public class QueryChecker {
    private final RecommendationRepository repository;

    public QueryChecker(RecommendationRepository repository) {
        this.repository = repository;
    }

    /**
     * Проверяет одно условие правила для пользователя.
     *
     * @param userId идентификатор клиента
     * @param query  объект запроса с типом, аргументами и флагом negate
     * @return true, если условие выполнено (с учётом negate)
     * @throws IllegalArgumentException если тип запроса неизвестен
     */
    public Boolean check(UUID userId, QueryDto query) {
        boolean result = switch (query.getQuery()) {
            case "USER_OF" -> {
                String productType = query.getArguments().get(0);
                int count = repository.countTransactionsByUserAndProductType(userId, productType);
                yield count > 0;
            }
            case "ACTIVE_USER_OF" -> {
                String productType = query.getArguments().get(0);
                int count = repository.countTransactionsByUserAndProductType(userId, productType);
                yield count >= 5;
            }
            case "TRANSACTION_SUM_COMPARE" -> {
                String productType = query.getArguments().get(0);
                String transactType = query.getArguments().get(1);
                ComparisonOperator operation = ComparisonOperator.fromSymbol(query.getArguments().get(2));
                int numberCompare = Integer.parseInt(query.getArguments().get(3));
                yield repository.checkTransactionSumCompare(userId, productType, transactType, operation, numberCompare);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                String productType = query.getArguments().get(0);
                ComparisonOperator operation = ComparisonOperator.fromSymbol(query.getArguments().get(1));
                int sumDeposit = repository.sumTransactionByUserAndProductType(userId, productType, "DEPOSIT");
                int sumWithdraw = repository.sumTransactionByUserAndProductType(userId, productType, "WITHDRAW");
                yield operation.compareQuantities(sumDeposit, sumWithdraw);
            }
            default -> throw new IllegalArgumentException("Unknown query type: " + query.getQuery());
        };

        return query.isNegate() != result;
    }
}
