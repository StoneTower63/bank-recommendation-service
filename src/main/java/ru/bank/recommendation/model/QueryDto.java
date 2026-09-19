package ru.bank.recommendation.model;

import java.util.List;
import java.util.Objects;

public class QueryDto {
    private String query;
    private List<Object> arguments;
    private boolean negate;

    public QueryDto() {
    }

    public QueryDto(String query, List<Object> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QueryDto queryDto = (QueryDto) o;
        return negate == queryDto.negate && Objects.equals(query, queryDto.query) && Objects.equals(arguments, queryDto.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, arguments, negate);
    }
}
