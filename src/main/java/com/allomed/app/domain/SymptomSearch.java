package com.allomed.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SymptomSearch.
 */
@Entity
@Table(name = "symptom_search")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "symptomsearch")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SymptomSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "search_date", nullable = false)
    private Instant searchDate;

    @NotNull
    @Column(name = "symptoms", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String symptoms;

    @Lob
    @Column(name = "ai_response_json", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String aiResponseJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "internalUser" }, allowSetters = true)
    private AppUserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    private GuestSession guestSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SymptomSearch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSearchDate() {
        return this.searchDate;
    }

    public SymptomSearch searchDate(Instant searchDate) {
        this.setSearchDate(searchDate);
        return this;
    }

    public void setSearchDate(Instant searchDate) {
        this.searchDate = searchDate;
    }

    public String getSymptoms() {
        return this.symptoms;
    }

    public SymptomSearch symptoms(String symptoms) {
        this.setSymptoms(symptoms);
        return this;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getAiResponseJson() {
        return this.aiResponseJson;
    }

    public SymptomSearch aiResponseJson(String aiResponseJson) {
        this.setAiResponseJson(aiResponseJson);
        return this;
    }

    public void setAiResponseJson(String aiResponseJson) {
        this.aiResponseJson = aiResponseJson;
    }

    public AppUserProfile getUser() {
        return this.user;
    }

    public void setUser(AppUserProfile appUserProfile) {
        this.user = appUserProfile;
    }

    public SymptomSearch user(AppUserProfile appUserProfile) {
        this.setUser(appUserProfile);
        return this;
    }

    public GuestSession getGuestSession() {
        return this.guestSession;
    }

    public void setGuestSession(GuestSession guestSession) {
        this.guestSession = guestSession;
    }

    public SymptomSearch guestSession(GuestSession guestSession) {
        this.setGuestSession(guestSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymptomSearch)) {
            return false;
        }
        return getId() != null && getId().equals(((SymptomSearch) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SymptomSearch{" +
            "id=" + getId() +
            ", searchDate='" + getSearchDate() + "'" +
            ", symptoms='" + getSymptoms() + "'" +
            ", aiResponseJson='" + getAiResponseJson() + "'" +
            "}";
    }
}
