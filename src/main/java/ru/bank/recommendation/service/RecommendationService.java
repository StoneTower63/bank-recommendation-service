package ru.bank.recommendation.service;

import org.springframework.stereotype.Service;
import ru.bank.recommendation.model.QueryDto;
import ru.bank.recommendation.model.RecommendationDto;
import ru.bank.recommendation.model.RecommendationResponse;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.repository.RuleRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис формирования рекомендаций для клиента.
 *
 * Загружает все динамические правила из PostgreSQL, проверяет каждое
 * через {@link QueryChecker}, инкрементирует статистику срабатываний
 * и формирует итоговый список рекомендованных продуктов.
 */
@Service
public class RecommendationService {
    private final RuleRepository ruleRepository;
    private final QueryChecker queryChecker;
    private final ObjectMapper objectMapper;
    private final RuleStatsService ruleStatsService;

    public RecommendationService(RuleRepository ruleRepository, QueryChecker queryChecker, ObjectMapper objectMapper, RuleStatsService ruleStatsService) {
        this.ruleRepository = ruleRepository;
        this.queryChecker = queryChecker;
        this.objectMapper = objectMapper;
        this.ruleStatsService = ruleStatsService;
    }

    /**
     * Возвращает рекомендации для указанного пользователя.
     *
     * Алгоритм:
     * <ol>
     *     <li>Загружает все правила из БД.</li>
     *     <li>Для каждого правила проверяет все условия (query).</li>
     *     <li>Если все условия выполнены — инкрементирует счётчик правила.</li>
     *     <li>Формирует список рекомендаций.</li>
     * </ol>
     *
     * @param userId идентификатор клиента
     * @return объект с userId и списком рекомендаций (может быть пустым)
     */
    public RecommendationResponse getUserRecommendations(UUID userId) {
        List<RuleEntity> listRule = ruleRepository.findAll();

        if (listRule.isEmpty()) {
            return new RecommendationResponse(userId, Collections.emptyList());
        }

        TypeReference<List<QueryDto>> typeRef = new TypeReference<List<QueryDto>>() {
        };

        List<RecommendationDto> matchedRecommendations = listRule.stream()
                .filter(rule -> areAllQueriesPassed(userId, rule.getRule(), queryChecker, objectMapper, typeRef))
                .peek(rule -> ruleStatsService.incrementCount(rule.getId()))
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
