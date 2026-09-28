package ru.bank.recommendation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.recommendation.model.RuleEntity;
import ru.bank.recommendation.model.RuleStatsEntity;
import ru.bank.recommendation.repository.RuleRepository;
import ru.bank.recommendation.repository.RuleStatsRepository;

@Service
public class RuleStatsService {

    private final RuleStatsRepository ruleStatsRepository;
    private final RuleRepository ruleRepository;

    public RuleStatsService(RuleStatsRepository ruleStatsRepository,
                            RuleRepository ruleRepository) {
        this.ruleStatsRepository = ruleStatsRepository;
        this.ruleRepository = ruleRepository;
    }

    @Transactional
    public void incrementCount(Long ruleId) {
        int updated = ruleStatsRepository.incrementCount(ruleId);

        if (updated == 0) {
            RuleEntity rule = ruleRepository.findById(ruleId)
                    .orElseThrow(() -> new IllegalStateException("Rule not found: " + ruleId));

            RuleStatsEntity stats = new RuleStatsEntity();
            stats.setRule(rule);
            stats.setCount(1L);

            ruleStatsRepository.save(stats);
        }
    }
}