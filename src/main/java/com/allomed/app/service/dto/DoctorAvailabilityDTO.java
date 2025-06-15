package com.allomed.app.service.dto;

import com.allomed.app.domain.enumeration.DayOfWeek;
import com.allomed.app.domain.enumeration.RecurrenceType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.DoctorAvailability} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorAvailabilityDTO implements Serializable {

    private Long id;

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Boolean isAvailable;

    @NotNull
    private RecurrenceType recurrenceType;

    @NotNull
    private LocalDate validFrom;

    private LocalDate validTo;

    private String doctorId;

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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorAvailabilityDTO)) {
            return false;
        }

        DoctorAvailabilityDTO doctorAvailabilityDTO = (DoctorAvailabilityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, doctorAvailabilityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "DoctorAvailabilityDTO{" +
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
            ", doctorId='" +
            getDoctorId() +
            "'" +
            "}"
        );
    }
}
