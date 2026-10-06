package ru.bank.recommendation.model;

import jakarta.persistence.*;

/**
 * JPA-сущность статистики срабатываний правила.
 *
 * Хранится в таблице {@code rule_stats} (PostgreSQL). Связана
 * с {@link RuleEntity} через FK {@code rule_id}.
 */
@Entity
@Table(name = "rule_stats")
public class RuleStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false, unique = true)
    private RuleEntity rule;

    @Column(name = "count", nullable = false)
    private Long count = 0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RuleEntity getRule() {
        return rule;
    }

    public void setRule(RuleEntity rule) {
        this.rule = rule;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}