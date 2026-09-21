package ru.bank.recommendation.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.bank.recommendation.enums.ComparisonOperator;
import ru.bank.recommendation.model.RecommendationDto;

import java.util.List;
import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RecommendationDto> getRecommendation(UUID id) {
        return List.of();
    }

    public int countTransactionsByUserAndProductType(UUID userId, String productType) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions t JOIN products p ON t.product_id = p.id AND p.type = ? WHERE t.user_id = ?"
                , Integer.class, productType, userId);
        return result != null ? result : 0;
    }

    public int sumTransactionByUserAndProductType(UUID userId, String productType, String transactType) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT sum(t.amount) FROM transactions t JOIN products p ON t.product_id = p.id AND p.type = ? WHERE t.user_id = ? AND t.type = ?"
                , Integer.class, productType, userId, transactType);
        return result != null ? result : 0;
    }

    public boolean checkTransactionSumCompare(UUID userId, String productType, String transactionType, ComparisonOperator operation, int numCompared) {
        String sql = "SELECT CASE WHEN SUM(t.amount) %s ? THEN 1 ELSE 0 END AS res FROM transactions t JOIN products p ON t.product_id = p.id AND p.type = ? WHERE t.user_id = ? AND t.type = ?";
        Boolean result = jdbcTemplate.queryForObject(String.format(sql, operation.getSymbol()), Boolean.class, numCompared, productType, userId, transactionType);
        return Boolean.TRUE.equals(result);
    }
}
