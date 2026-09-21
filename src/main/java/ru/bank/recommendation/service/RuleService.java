package ru.bank.recommendation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bank.recommendation.model.QueryDto;
import ru.bank.recommendation.model.RuleDto;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.model.RulesResponseDto;
import ru.bank.recommendation.repository.RuleRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RuleService(RuleRepository ruleRepository, ObjectMapper objectMapper) {
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
    }

    public RuleDto createRule(RuleDto ruleDto) {
        // 1. Создаем и заполняем Entity
        RuleEntity entity = new RuleEntity();
        entity.setProductName(ruleDto.getProductName());
        entity.setProductId(ruleDto.getProductId());
        entity.setProductText(ruleDto.getProductText());

        // 2. Конвертируем List<QueryDto> в JSON-строку
        entity.setRule(convertListToJson(ruleDto.getRule()));

        // 3. Сохраняем (база сгенерирует id)
        RuleEntity savedEntity = ruleRepository.save(entity);

        // 4. Создаем DTO для ответа
        RuleDto resultDto = new RuleDto();
        resultDto.setId(savedEntity.getId());
        resultDto.setProductName(savedEntity.getProductName());
        resultDto.setProductId(savedEntity.getProductId());
        resultDto.setProductText(savedEntity.getProductText());

        // 5. Превращаем JSON-строку обратно в List<QueryDto>
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

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }
}