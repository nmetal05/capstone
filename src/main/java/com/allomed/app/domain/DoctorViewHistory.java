package com.allomed.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DoctorViewHistory.
 */
@Entity
@Table(name = "doctor_view_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "doctorviewhistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorViewHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "view_date", nullable = false)
    private Instant viewDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "internalUser" }, allowSetters = true)
    private AppUserProfile user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "internalUser", "specializations" }, allowSetters = true)
    private DoctorProfile doctor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DoctorViewHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getViewDate() {
        return this.viewDate;
    }

    public DoctorViewHistory viewDate(Instant viewDate) {
        this.setViewDate(viewDate);
        return this;
    }

    public void setViewDate(Instant viewDate) {
        this.viewDate = viewDate;
    }

    public AppUserProfile getUser() {
        return this.user;
    }

    public void setUser(AppUserProfile appUserProfile) {
        this.user = appUserProfile;
    }

    public DoctorViewHistory user(AppUserProfile appUserProfile) {
        this.setUser(appUserProfile);
        return this;
    }

    public DoctorProfile getDoctor() {
        return this.doctor;
    }

    public void setDoctor(DoctorProfile doctorProfile) {
        this.doctor = doctorProfile;
    }

    public DoctorViewHistory doctor(DoctorProfile doctorProfile) {
        this.setDoctor(doctorProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorViewHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((DoctorViewHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorViewHistory{" +
            "id=" + getId() +
            ", viewDate='" + getViewDate() + "'" +
            "}";
    }
}
