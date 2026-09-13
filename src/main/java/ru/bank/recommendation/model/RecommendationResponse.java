package ru.bank.recommendation.model;

import java.util.List;

public class RecommendationResponse {
    private String user_id;
    private List<RecommendationDto> recommendationsDTO;

    public RecommendationResponse(String user_id, List<RecommendationDto> recommendationsDTO) {
        this.user_id = user_id;
        this.recommendationsDTO = recommendationsDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationResponse that = (RecommendationResponse) o;
        return user_id != null ? user_id.equals(that.user_id) : that.user_id == null;
    }

    @Override
    public int hashCode() {
        return user_id != null ? user_id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RecommendationResponse{" +
                "user_id='" + user_id + '\'' +
                ", recommendationsDTO=" + recommendationsDTO +
                '}';
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<RecommendationDto> getRecommendationsDTO() {
        return recommendationsDTO;
    }

    public void setRecommendationsDTO(List<RecommendationDto> recommendationsDTO) {
        this.recommendationsDTO = recommendationsDTO;
    }
}
