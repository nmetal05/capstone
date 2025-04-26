package com.allomed.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.SymptomSearch} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SymptomSearchDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant searchDate;

    @NotNull
    private String symptoms;

    @Lob
    private String aiResponseJson;

    private AppUserProfileDTO user;

    private GuestSessionDTO guestSession;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Instant searchDate) {
        this.searchDate = searchDate;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getAiResponseJson() {
        return aiResponseJson;
    }

    public void setAiResponseJson(String aiResponseJson) {
        this.aiResponseJson = aiResponseJson;
    }

    public AppUserProfileDTO getUser() {
        return user;
    }

    public void setUser(AppUserProfileDTO user) {
        this.user = user;
    }

    public GuestSessionDTO getGuestSession() {
        return guestSession;
    }

    public void setGuestSession(GuestSessionDTO guestSession) {
        this.guestSession = guestSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymptomSearchDTO)) {
            return false;
        }

        SymptomSearchDTO symptomSearchDTO = (SymptomSearchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, symptomSearchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SymptomSearchDTO{" +
            "id=" + getId() +
            ", searchDate='" + getSearchDate() + "'" +
            ", symptoms='" + getSymptoms() + "'" +
            ", aiResponseJson='" + getAiResponseJson() + "'" +
            ", user=" + getUser() +
            ", guestSession=" + getGuestSession() +
            "}";
    }
}
