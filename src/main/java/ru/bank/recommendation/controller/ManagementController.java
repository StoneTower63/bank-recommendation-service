package ru.bank.recommendation.controller;

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
                                BuildProperties buildProperties) {
        this.recommendationRepository = recommendationRepository;
        this.buildProperties = buildProperties;
    }

    // #57
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationRepository.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    // #58
    @GetMapping("/info")
    public ResponseEntity<ServiceInfoResponse> getInfo() {
        return ResponseEntity.ok(new ServiceInfoResponse(
                buildProperties.getName(),
                buildProperties.getVersion()
        ));
    }
}