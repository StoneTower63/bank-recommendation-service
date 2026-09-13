package ru.bank.recommendation.model;

import java.util.List;

public class RecommendationResponse {
    private String userId;
    private List<RecommendationDto> recommendationsDTO;

    public RecommendationResponse(String userId, List<RecommendationDto> recommendationsDTO) {
        this.userId = userId;
        this.recommendationsDTO = recommendationsDTO;
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
                "userId='" + userId + '\'' +
                ", recommendationsDTO=" + recommendationsDTO +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<RecommendationDto> getRecommendationsDTO() {
        return recommendationsDTO;
    }

    public void setRecommendationsDTO(List<RecommendationDto> recommendationsDTO) {
        this.recommendationsDTO = recommendationsDTO;
    }
}
