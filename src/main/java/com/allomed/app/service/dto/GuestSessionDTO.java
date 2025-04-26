package com.allomed.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.GuestSession} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GuestSessionDTO implements Serializable {

    private Long id;

    @NotNull
    private String sessionId;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant lastActiveAt;

    @NotNull
    private String ipAddress;

    @NotNull
    private String userAgent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuestSessionDTO)) {
            return false;
        }

        GuestSessionDTO guestSessionDTO = (GuestSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, guestSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuestSessionDTO{" +
            "id=" + getId() +
            ", sessionId='" + getSessionId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", lastActiveAt='" + getLastActiveAt() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            "}";
    }
}
