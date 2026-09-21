package ru.bank.recommendation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RuleDto {
    private Long id;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_id")
    private UUID productId;
    @JsonProperty("product_text")
    private String productText;
    private List<QueryDto> rule;

    public RuleDto() {
    }

    public RuleDto(String productName, UUID productId, String productText, List<QueryDto> rule) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
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

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<QueryDto> getRule() {
        return rule;
    }

    public void setRule(List<QueryDto> rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuleDto ruleDto = (RuleDto) o;
        return Objects.equals(productName, ruleDto.productName) && Objects.equals(productId, ruleDto.productId) && Objects.equals(productText, ruleDto.productText) && Objects.equals(rule, ruleDto.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productId, productText, rule);
    }
}
