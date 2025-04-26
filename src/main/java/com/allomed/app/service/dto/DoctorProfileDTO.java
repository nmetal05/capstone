package com.allomed.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.allomed.app.domain.DoctorProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorProfileDTO implements Serializable {

    private String id;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String officeAddress;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private String inpeCode;

    @NotNull
    private Boolean isVerified;

    private String lastLoginIp;

    private String lastUserAgent;

    private Instant lastLoginDate;

    private UserDTO internalUser;

    private Set<SpecializationDTO> specializations = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getInpeCode() {
        return inpeCode;
    }

    public void setInpeCode(String inpeCode) {
        this.inpeCode = inpeCode;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastUserAgent() {
        return lastUserAgent;
    }

    public void setLastUserAgent(String lastUserAgent) {
        this.lastUserAgent = lastUserAgent;
    }

    public Instant getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Instant lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public UserDTO getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(UserDTO internalUser) {
        this.internalUser = internalUser;
    }

    public Set<SpecializationDTO> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<SpecializationDTO> specializations) {
        this.specializations = specializations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorProfileDTO)) {
            return false;
        }

        DoctorProfileDTO doctorProfileDTO = (DoctorProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, doctorProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorProfileDTO{" +
            "id='" + getId() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", officeAddress='" + getOfficeAddress() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", inpeCode='" + getInpeCode() + "'" +
            ", isVerified='" + getIsVerified() + "'" +
            ", lastLoginIp='" + getLastLoginIp() + "'" +
            ", lastUserAgent='" + getLastUserAgent() + "'" +
            ", lastLoginDate='" + getLastLoginDate() + "'" +
            ", internalUser=" + getInternalUser() +
            ", specializations=" + getSpecializations() +
            "}";
    }
}
