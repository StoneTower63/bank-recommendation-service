package ru.bank.recommendation.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bank.recommendation.cache.CacheKey;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class CaffeineTest {
    private Cache<CacheKey, Object> cache;
    private AtomicInteger dbCallCounter;

    @BeforeEach
    void setUp() {
        this.dbCallCounter = new AtomicInteger(0);
        this.cache = Caffeine.newBuilder().maximumSize(100).build();
    }

    @Test
    void firstRequestShouldMissCache_SecondShouldHit() {
        CacheKey key = new CacheKey("user-1", "DEPOSIT");
        Object result1 = getFromDbOrCache(key);
        assertThat(dbCallCounter).hasValue(1);
        assertThat(cache.estimatedSize()).isEqualTo(1);

        Object result2 = getFromDbOrCache(key);
        assertThat(dbCallCounter).hasValue(1);
        assertThat(result2).isSameAs(result1);
    }

    private Object getFromDbOrCache(CacheKey key) {
        return cache.get(key, k -> {
            dbCallCounter.incrementAndGet();
            return new Object();
        });
    }
}
