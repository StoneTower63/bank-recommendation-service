package ru.bank.recommendation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class RecommendationResponse {
    private UUID userId;
    private List<RecommendationDto> recommendations;

    public RecommendationResponse(UUID userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationResponse that = (RecommendationResponse) o;
        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RecommendationResponse{" +
                "user_id='" + userId + '\'' +
                ", recommendations=" + recommendations +
                '}';
    }

    @JsonProperty("user_id")
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<RecommendationDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }
}
