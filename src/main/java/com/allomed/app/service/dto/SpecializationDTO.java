package com.allomed.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.allomed.app.domain.Specialization} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpecializationDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private Set<DoctorProfileDTO> doctorProfiles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<DoctorProfileDTO> getDoctorProfiles() {
        return doctorProfiles;
    }

    public void setDoctorProfiles(Set<DoctorProfileDTO> doctorProfiles) {
        this.doctorProfiles = doctorProfiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpecializationDTO)) {
            return false;
        }

        SpecializationDTO specializationDTO = (SpecializationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, specializationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpecializationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", doctorProfiles=" + getDoctorProfiles() +
            "}";
    }
}
