package ru.bank.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.recommendation.repository.RecommendationRepository;

import java.util.UUID;

@RestController
@RequestMapping("/test-cache")
public class CacheTestController {
    private final RecommendationRepository repository;

    public CacheTestController(RecommendationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/count/{userId}/{productType}")
    public int testCount(
            @PathVariable UUID userId,
            @PathVariable String productType) {

        System.out.println(" ЗАПРОС ПРИШЕЛ В КОНТРОЛЛЕР");
        return repository.countTransactionsByUserAndProductType(userId, productType);
    }

}
