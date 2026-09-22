package ru.bank.recommendation.cache;

import java.util.Objects;

public class CacheKey {
    private final String userId;
    private final String productType;
    private final String transactionType;

    public CacheKey(String userId, String productType) {
        this.userId = userId;
        this.productType = productType;
        this.transactionType = null;
    }

    public CacheKey(String userId, String productType, String transactionType) {
        this.userId = userId;
        this.productType = productType;
        this.transactionType = transactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(userId, cacheKey.userId) &&
                Objects.equals(productType, cacheKey.productType) &&
                Objects.equals(transactionType, cacheKey.transactionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productType, transactionType);
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "userId='" + userId + '\'' +
                ", productType='" + productType + '\'' +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}