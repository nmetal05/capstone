package com.allomed.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SymptomSearchRecommendation.
 */
@Entity
@Table(name = "symptom_search_recommendation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "symptomsearchrecommendation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SymptomSearchRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore;

    @NotNull
    @Column(name = "rank", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer rank;

    @NotNull
    @Column(name = "reasoning", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String reasoning;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "guestSession" }, allowSetters = true)
    private SymptomSearch search;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "doctorProfiles" }, allowSetters = true)
    private Specialization specialization;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SymptomSearchRecommendation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getConfidenceScore() {
        return this.confidenceScore;
    }

    public SymptomSearchRecommendation confidenceScore(Double confidenceScore) {
        this.setConfidenceScore(confidenceScore);
        return this;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getRank() {
        return this.rank;
    }

    public SymptomSearchRecommendation rank(Integer rank) {
        this.setRank(rank);
        return this;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getReasoning() {
        return this.reasoning;
    }

    public SymptomSearchRecommendation reasoning(String reasoning) {
        this.setReasoning(reasoning);
        return this;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public SymptomSearch getSearch() {
        return this.search;
    }

    public void setSearch(SymptomSearch symptomSearch) {
        this.search = symptomSearch;
    }

    public SymptomSearchRecommendation search(SymptomSearch symptomSearch) {
        this.setSearch(symptomSearch);
        return this;
    }

    public Specialization getSpecialization() {
        return this.specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public SymptomSearchRecommendation specialization(Specialization specialization) {
        this.setSpecialization(specialization);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymptomSearchRecommendation)) {
            return false;
        }
        return getId() != null && getId().equals(((SymptomSearchRecommendation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SymptomSearchRecommendation{" +
            "id=" + getId() +
            ", confidenceScore=" + getConfidenceScore() +
            ", rank=" + getRank() +
            ", reasoning='" + getReasoning() + "'" +
            "}";
    }
}
