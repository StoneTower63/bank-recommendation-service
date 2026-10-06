package ru.bank.recommendation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bank.recommendation.model.RuleDto;
import ru.bank.recommendation.model.RuleStatsResponse;
import ru.bank.recommendation.model.RulesResponseDto;
import ru.bank.recommendation.service.RuleService;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 *
 * Предоставляет эндпоинты:
 * <ul>
 *     <li>{@code POST /rule} — создание правила;</li>
 *     <li>{@code GET /rule} — список всех правил;</li>
 *     <li>{@code DELETE /rule/{id}} — удаление правила;</li>
 *     <li>{@code GET /rule/stats} — статистика срабатываний.</li>
 * </ul>
 */
@RestController
@RequestMapping("/rule")
public class RuleController {
    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Создаёт новое динамическое правило.
     *
     * @param ruleDto правило для сохранения
     * @return сохранённое правило с присвоенным id
     */
    @PostMapping
    public ResponseEntity<RuleDto> createRule(@RequestBody RuleDto ruleDto) {
        RuleDto savedRule = ruleService.createRule(ruleDto);
        return ResponseEntity.status(HttpStatus.OK).body(savedRule);
    }

    /**
     * Возвращает список всех динамических правил.
     *
     * @return объект с массивом правил
     */
    @GetMapping
    public ResponseEntity<RulesResponseDto> getAllRules() {
        RulesResponseDto rules = ruleService.getAllRules();
        return ResponseEntity.status(HttpStatus.OK).body(rules);
    }

    /**
     * Удаляет правило по id вместе со связанной статистикой.
     *
     * @param id идентификатор правила
     * @return пустой ответ со статусом 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Возвращает статистику срабатываний по всем правилам.
     *
     * Включает правила с count = 0 (LEFT JOIN).
     *
     * @return объект со списком статистики
     */
    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getStats() {
        return ResponseEntity.ok(ruleService.getStats());
    }
}
