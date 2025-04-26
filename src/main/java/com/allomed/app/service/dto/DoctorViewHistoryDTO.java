package com.allomed.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.DoctorViewHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorViewHistoryDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant viewDate;

    @NotNull
    private AppUserProfileDTO user;

    @NotNull
    private DoctorProfileDTO doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getViewDate() {
        return viewDate;
    }

    public void setViewDate(Instant viewDate) {
        this.viewDate = viewDate;
    }

    public AppUserProfileDTO getUser() {
        return user;
    }

    public void setUser(AppUserProfileDTO user) {
        this.user = user;
    }

    public DoctorProfileDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorProfileDTO doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorViewHistoryDTO)) {
            return false;
        }

        DoctorViewHistoryDTO doctorViewHistoryDTO = (DoctorViewHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, doctorViewHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorViewHistoryDTO{" +
            "id=" + getId() +
            ", viewDate='" + getViewDate() + "'" +
            ", user=" + getUser() +
            ", doctor=" + getDoctor() +
            "}";
    }
}
