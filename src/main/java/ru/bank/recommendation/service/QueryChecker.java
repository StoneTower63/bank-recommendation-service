package ru.bank.recommendation.service;

import org.springframework.stereotype.Service;
import ru.bank.recommendation.enums.ComparisonOperator;
import ru.bank.recommendation.model.QueryDto;
import ru.bank.recommendation.repository.RecommendationRepository;

import java.util.UUID;

@Service
public class QueryChecker {
    private final RecommendationRepository repository;

    public QueryChecker(RecommendationRepository repository) {
        this.repository = repository;
    }

    public Boolean check(UUID userId, QueryDto query) {
        boolean result = switch (query.getQuery()) {
            case "USER_OF" -> {
                String productType = (String) query.getArguments().get(0);
                int count = repository.countTransactionsByUserAndProductType(userId, productType);
                yield count > 0;
            }
            case "ACTIVE_USER_OF" -> {
                String productType = (String) query.getArguments().get(0);
                int count = repository.countTransactionsByUserAndProductType(userId, productType);
                yield count >= 5;
            }
            case "TRANSACTION_SUM_COMPARE" -> {
                String productType = (String) query.getArguments().get(0);
                String transactType = (String) query.getArguments().get(1);
                ComparisonOperator operation = ComparisonOperator.fromSymbol((String) query.getArguments().get(2));
                int numberCompare = (int) query.getArguments().get(3);
                yield repository.checkTransactionSumCompare(userId, productType, transactType, operation, numberCompare);
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                String productType = (String) query.getArguments().get(0);
                ComparisonOperator operation = ComparisonOperator.fromSymbol((String) query.getArguments().get(1));
                int sumDeposit = repository.sumTransactionByUserAndProductType(userId, productType, "DEPOSIT");
                int sumWithdraw = repository.sumTransactionByUserAndProductType(userId, productType, "WITHDRAW");
                yield operation.compareQuantities(sumDeposit, sumWithdraw);
            }
            default -> throw new IllegalArgumentException("Unknown query type: " + query.getQuery());
        };

        return query.isNegate() ? !result : result;
    }
}
