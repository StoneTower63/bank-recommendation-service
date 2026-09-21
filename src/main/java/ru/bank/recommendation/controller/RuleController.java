package ru.bank.recommendation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bank.recommendation.model.RuleDto;
import ru.bank.recommendation.model.RulesResponseDto;
import ru.bank.recommendation.service.RuleService;

@RestController
@RequestMapping("/rule")
public class RuleController {
    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    public ResponseEntity<RuleDto> createRule(@RequestBody RuleDto ruleDto) {
        RuleDto savedRule = ruleService.createRule(ruleDto);
        return ResponseEntity.status(HttpStatus.OK).body(savedRule);
    }
    @GetMapping
    public ResponseEntity<RulesResponseDto> getAllRules(){
        RulesResponseDto rules = ruleService.getAllRules();
        return ResponseEntity.status(HttpStatus.OK).body(rules);
    }
}
