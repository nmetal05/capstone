package com.allomed.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.SymptomSearchRecommendation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SymptomSearchRecommendationDTO implements Serializable {

    private Long id;

    @NotNull
    private Double confidenceScore;

    @NotNull
    private Integer rank;

    @NotNull
    private String reasoning;

    @NotNull
    private SymptomSearchDTO search;

    @NotNull
    private SpecializationDTO specialization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public SymptomSearchDTO getSearch() {
        return search;
    }

    public void setSearch(SymptomSearchDTO search) {
        this.search = search;
    }

    public SpecializationDTO getSpecialization() {
        return specialization;
    }

    public void setSpecialization(SpecializationDTO specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymptomSearchRecommendationDTO)) {
            return false;
        }

        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = (SymptomSearchRecommendationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, symptomSearchRecommendationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SymptomSearchRecommendationDTO{" +
            "id=" + getId() +
            ", confidenceScore=" + getConfidenceScore() +
            ", rank=" + getRank() +
            ", reasoning='" + getReasoning() + "'" +
            ", search=" + getSearch() +
            ", specialization=" + getSpecialization() +
            "}";
    }
}
