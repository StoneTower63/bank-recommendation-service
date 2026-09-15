package ru.bank.recommendation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.recommendation.model.RecommendationDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    @GetMapping("/{user_id}")
    public List<RecommendationDto> getRecommendation(@PathVariable("user_id") UUID userId) {
        return List.of();
    }
}