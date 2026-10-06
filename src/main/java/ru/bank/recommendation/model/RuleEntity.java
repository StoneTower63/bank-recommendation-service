package ru.bank.recommendation.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * JPA-сущность динамического правила рекомендации.
 *
 * Хранится в таблице {@code rules} (PostgreSQL). Поле {@code rule}
 * содержит JSON-массив запросов и мапится как {@code jsonb}.
 */
@Entity
@Table(name = "rules")
public class RuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Column(name = "product_text", length = 1000)
    private String productText;
    @Column(name = "rule", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String rule;

    public RuleEntity() {
    }

    public RuleEntity(String productName, UUID productId, String productText, String rule) {

        this.productName = productName;
        this.rule = rule;
        this.productId = productId;
        this.productText = productText;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

}
