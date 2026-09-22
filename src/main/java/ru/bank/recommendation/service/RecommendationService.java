package ru.bank.recommendation.service;

import org.springframework.stereotype.Service;
import ru.bank.recommendation.model.QueryDto;
import ru.bank.recommendation.model.RecommendationDto;
import ru.bank.recommendation.model.RecommendationResponse;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.repository.RuleRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final RuleRepository ruleRepository;
    private final QueryChecker queryChecker;
    private final ObjectMapper objectMapper;

    public RecommendationService(RuleRepository ruleRepository, QueryChecker queryChecker, ObjectMapper objectMapper) {
        this.ruleRepository = ruleRepository;
        this.queryChecker = queryChecker;
        this.objectMapper = objectMapper;
    }

    public RecommendationResponse getUserRecommendations(UUID userId) {
        List<RuleEntity> listRule = ruleRepository.findAll();

        if (listRule.isEmpty()) {
            return new RecommendationResponse(userId, Collections.emptyList());
        }

        TypeReference<List<QueryDto>> typeRef = new TypeReference<List<QueryDto>>() {
        };

        List<RecommendationDto> matchedRecommendations = listRule.stream()
                .filter(rule -> areAllQueriesPassed(userId, rule.getRule(), queryChecker, objectMapper, typeRef))
                .map(this::toRecommendationDto)
                .collect(Collectors.toList());

        return new RecommendationResponse(userId, matchedRecommendations);

    }

    private boolean areAllQueriesPassed(UUID userId, String ruleJson, QueryChecker checker,
                                        ObjectMapper mapper, TypeReference<List<QueryDto>> typeRef) {
        if (ruleJson == null || ruleJson.trim().isEmpty()) {
            return false;
        }

        List<QueryDto> queries = mapper.readValue(ruleJson, typeRef);

        if (queries.isEmpty()) {
            return false;
        }

        return queries.stream().allMatch(query -> checker.check(userId, query));
    }

    private RecommendationDto toRecommendationDto(RuleEntity entity) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(entity.getProductId());
        dto.setName(entity.getProductName());
        dto.setText(entity.getProductText());
        return dto;
    }

}
