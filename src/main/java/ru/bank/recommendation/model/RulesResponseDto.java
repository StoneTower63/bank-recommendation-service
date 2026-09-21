package ru.bank.recommendation.model;

import java.util.List;

public class RulesResponseDto {
    private List<RuleDto> data;

    public RulesResponseDto(List<RuleDto> data) {
        this.data = data;
    }

    public List<RuleDto> getData() {
        return data;
    }

    public void setData(List<RuleDto> data) {
        this.data = data;
    }
}
