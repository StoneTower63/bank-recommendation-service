package ru.bank.recommendation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.recommendation.model.*;
import ru.bank.recommendation.repository.RuleRepository;
import ru.bank.recommendation.repository.RuleStatsRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис управления динамическими правилами рекомендаций.
 *
 * Обеспечивает CRUD правил в PostgreSQL и возвращает статистику
 * срабатываний по всем правилам.
 */
@Service
public class RuleService {
    private final RuleRepository ruleRepository;
    private final ObjectMapper objectMapper;
    private final RuleStatsRepository ruleStatsRepository;

    @Autowired
    public RuleService(RuleRepository ruleRepository, ObjectMapper objectMapper, RuleStatsRepository ruleStatsRepository) {
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    /**
     * Создаёт новое динамическое правило.
     *
     * Конвертирует список запросов {@link ru.bank.recommendation.model.QueryDto}
     * в JSON-строку для хранения в колонке {@code jsonb}.
     *
     * @param ruleDto правило для сохранения
     * @return сохранённое правило с присвоенным id
     */
    public RuleDto createRule(RuleDto ruleDto) {
        RuleEntity entity = new RuleEntity();
        entity.setProductName(ruleDto.getProductName());
        entity.setProductId(ruleDto.getProductId());
        entity.setProductText(ruleDto.getProductText());

        entity.setRule(convertListToJson(ruleDto.getRule()));

        RuleEntity savedEntity = ruleRepository.save(entity);

        RuleDto resultDto = new RuleDto();
        resultDto.setId(savedEntity.getId());
        resultDto.setProductName(savedEntity.getProductName());
        resultDto.setProductId(savedEntity.getProductId());
        resultDto.setProductText(savedEntity.getProductText());

        resultDto.setRule(convertJsonToList(savedEntity.getRule()));

        return resultDto;
    }

    private String convertListToJson(List<QueryDto> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JacksonException e) {
            throw new RuntimeException("Ошибка JSON", e);
        }
    }

    private List<QueryDto> convertJsonToList(String json) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class, QueryDto.class
                    )
            );
        } catch (JacksonException e) {
            throw new RuntimeException("Ошибка JSON", e);
        }
    }

    /**
     * Возвращает список всех динамических правил из БД.
     *
     * @return объект с массивом правил
     */
    public RulesResponseDto getAllRules() {
        List<RuleEntity> listEntity = ruleRepository.findAll();
        List<RuleDto> listRuleDto = new ArrayList<>();
        for (RuleEntity entity : listEntity) {
            RuleDto ruleDto = new RuleDto();
            ruleDto.setId(entity.getId());
            ruleDto.setProductName(entity.getProductName());
            ruleDto.setProductId(entity.getProductId());
            ruleDto.setProductText(entity.getProductText());
            ruleDto.setRule(convertJsonToList(entity.getRule()));
            listRuleDto.add(ruleDto);
        }
        return new RulesResponseDto(listRuleDto);
    }

    /**
     * Удаляет правило по id вместе со связанной статистикой.
     *
     * @param id идентификатор правила
     */
    @Transactional
    public void deleteRule(Long id) {
        ruleStatsRepository.deleteByRuleId(id);
        ruleRepository.deleteById(id);
    }

    /**
     * Возвращает статистику срабатываний по всем правилам.
     *
     * Включает правила с count = 0 (LEFT JOIN).
     *
     * @return объект со списком статистики
     */
    public RuleStatsResponse getStats() {
        List<RuleStatsDto> stats = ruleStatsRepository.findAllWithStats();
        return new RuleStatsResponse(stats);
    }
}