package ru.bank.recommendation.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.bank.recommendation.cache.CacheKey;
import ru.bank.recommendation.enums.ComparisonOperator;
import ru.bank.recommendation.model.RecommendationDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final Cache<CacheKey, Integer> countCache;

    private final Cache<CacheKey, Integer> sumCache;

    private final Cache<CacheKey, Boolean> checkCache;


    @Autowired
    public RecommendationRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.countCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();

        this.sumCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();

        this.checkCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    public List<RecommendationDto> getRecommendation(UUID id) {
        return List.of();
    }

    public int countTransactionsByUserAndProductType(UUID userId, String productType) {
        CacheKey key = new CacheKey(userId.toString(), productType);

        return countCache.asMap().computeIfAbsent(key, k -> {
            System.out.println("🔍 MISS (count): Запрос к БД для userId=" + userId + ", productType=" + productType);
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM transactions t JOIN products p ON t.product_id = p.id AND p.type = ? WHERE t.user_id = ?",
                    Integer.class, productType, userId);
            return result != null ? result : 0;
        });
    }

    public int sumTransactionByUserAndProductType(UUID userId, String productType, String transactType) {
        CacheKey key = new CacheKey(userId.toString(), productType, transactType);

        return sumCache.asMap().computeIfAbsent(key, k -> {
            System.out.println("🔍 MISS (sum): Запрос к БД для userId=" + userId + ", productType=" + productType);
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT sum(t.amount) FROM transactions t JOIN products p ON t.product_id = p.id AND p.type = ? WHERE t.user_id = ? AND t.type = ?",
                    Integer.class, productType, userId, transactType);
            return result != null ? result : 0;
        });
    }

    public boolean checkTransactionSumCompare(UUID userId, String productType, String transactionType, ComparisonOperator operation, int numCompared) {
        CacheKey key = new CacheKey(userId.toString(), productType, transactionType + "_" + operation + "_" + numCompared);

        return checkCache.asMap().computeIfAbsent(key, k -> {
            System.out.println("🔍 MISS (check): Запрос к БД для userId=" + userId);
            String sql = "SELECT CASE WHEN SUM(t.amount) %s ? THEN 1 ELSE 0 END AS res FROM transactions t JOIN products p ON t.product_id = p.id AND p.type = ? WHERE t.user_id = ? AND t.type = ?";
            Boolean result = jdbcTemplate.queryForObject(
                    String.format(sql, operation.getSymbol()),
                    Boolean.class, numCompared, productType, userId, transactionType);
            return Boolean.TRUE.equals(result);
        });
    }
}