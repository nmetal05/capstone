package com.allomed.app.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.AppUserProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUserProfileDTO implements Serializable {

    private String id;

    private Double latitude;

    private Double longitude;

    private String lastLoginIp;

    private String lastUserAgent;

    private Instant lastLoginDate;

    private UserDTO internalUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUserProfileDTO)) {
            return false;
        }

        AppUserProfileDTO appUserProfileDTO = (AppUserProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appUserProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserProfileDTO{" +
            "id='" + getId() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", lastLoginIp='" + getLastLoginIp() + "'" +
            ", lastUserAgent='" + getLastUserAgent() + "'" +
            ", lastLoginDate='" + getLastLoginDate() + "'" +
            ", internalUser=" + getInternalUser() +
            "}";
    }
}
