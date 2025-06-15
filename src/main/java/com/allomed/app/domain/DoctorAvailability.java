package com.allomed.app.domain;

import com.allomed.app.domain.enumeration.DayOfWeek;
import com.allomed.app.domain.enumeration.RecurrenceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A DoctorAvailability.
 */
@Entity
@Table(name = "doctor_availability")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorAvailability implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", nullable = false)
    private RecurrenceType recurrenceType;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "internalUser", "specializations", "documents" }, allowSetters = true)
    private DoctorProfile doctor;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public DoctorProfile getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorProfile doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorAvailability)) {
            return false;
        }
        return getId() != null && getId().equals(((DoctorAvailability) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "DoctorAvailability{" +
            "id=" +
            getId() +
            ", dayOfWeek='" +
            getDayOfWeek() +
            "'" +
            ", startTime='" +
            getStartTime() +
            "'" +
            ", endTime='" +
            getEndTime() +
            "'" +
            ", isAvailable='" +
            getIsAvailable() +
            "'" +
            ", recurrenceType='" +
            getRecurrenceType() +
            "'" +
            ", validFrom='" +
            getValidFrom() +
            "'" +
            ", validTo='" +
            getValidTo() +
            "'" +
            "}"
        );
    }
}
