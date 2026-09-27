package ru.bank.recommendation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.recommendation.repository.RecommendationRepository;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final RecommendationRepository recommendationRepository;

    public ManagementController(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }
}