package ru.bank.recommendation.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.recommendation.model.ServiceInfoResponse;
import ru.bank.recommendation.repository.RecommendationRepository;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final RecommendationRepository recommendationRepository;
    private final BuildProperties buildProperties;

    public ManagementController(RecommendationRepository recommendationRepository,
                                ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.recommendationRepository = recommendationRepository;
        this.buildProperties = buildPropertiesProvider.getIfAvailable();
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<ServiceInfoResponse> getInfo() {
        String name = buildProperties != null ? buildProperties.getName() : "bank-recommendation-service";
        String version = buildProperties != null ? buildProperties.getVersion() : "unknown";
        return ResponseEntity.ok(new ServiceInfoResponse(name, version));
    }
}