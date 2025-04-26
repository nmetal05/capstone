package com.allomed.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.allomed.app.domain.DoctorProfile} entity. This class is used
 * in {@link com.allomed.app.web.rest.DoctorProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /doctor-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorProfileCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter id;

    private StringFilter phoneNumber;

    private StringFilter officeAddress;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private StringFilter inpeCode;

    private BooleanFilter isVerified;

    private StringFilter lastLoginIp;

    private StringFilter lastUserAgent;

    private InstantFilter lastLoginDate;

    private StringFilter internalUserId;

    private LongFilter specializationsId;

    private Boolean distinct;

    public DoctorProfileCriteria() {}

    public DoctorProfileCriteria(DoctorProfileCriteria other) {
        this.id = other.optionalId().map(StringFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.officeAddress = other.optionalOfficeAddress().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(DoubleFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(DoubleFilter::copy).orElse(null);
        this.inpeCode = other.optionalInpeCode().map(StringFilter::copy).orElse(null);
        this.isVerified = other.optionalIsVerified().map(BooleanFilter::copy).orElse(null);
        this.lastLoginIp = other.optionalLastLoginIp().map(StringFilter::copy).orElse(null);
        this.lastUserAgent = other.optionalLastUserAgent().map(StringFilter::copy).orElse(null);
        this.lastLoginDate = other.optionalLastLoginDate().map(InstantFilter::copy).orElse(null);
        this.internalUserId = other.optionalInternalUserId().map(StringFilter::copy).orElse(null);
        this.specializationsId = other.optionalSpecializationsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DoctorProfileCriteria copy() {
        return new DoctorProfileCriteria(this);
    }

    public StringFilter getId() {
        return id;
    }

    public Optional<StringFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public StringFilter id() {
        if (id == null) {
            setId(new StringFilter());
        }
        return id;
    }

    public void setId(StringFilter id) {
        this.id = id;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getOfficeAddress() {
        return officeAddress;
    }

    public Optional<StringFilter> optionalOfficeAddress() {
        return Optional.ofNullable(officeAddress);
    }

    public StringFilter officeAddress() {
        if (officeAddress == null) {
            setOfficeAddress(new StringFilter());
        }
        return officeAddress;
    }

    public void setOfficeAddress(StringFilter officeAddress) {
        this.officeAddress = officeAddress;
    }

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public Optional<DoubleFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            setLatitude(new DoubleFilter());
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public Optional<DoubleFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            setLongitude(new DoubleFilter());
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public StringFilter getInpeCode() {
        return inpeCode;
    }

    public Optional<StringFilter> optionalInpeCode() {
        return Optional.ofNullable(inpeCode);
    }

    public StringFilter inpeCode() {
        if (inpeCode == null) {
            setInpeCode(new StringFilter());
        }
        return inpeCode;
    }

    public void setInpeCode(StringFilter inpeCode) {
        this.inpeCode = inpeCode;
    }

    public BooleanFilter getIsVerified() {
        return isVerified;
    }

    public Optional<BooleanFilter> optionalIsVerified() {
        return Optional.ofNullable(isVerified);
    }

    public BooleanFilter isVerified() {
        if (isVerified == null) {
            setIsVerified(new BooleanFilter());
        }
        return isVerified;
    }

    public void setIsVerified(BooleanFilter isVerified) {
        this.isVerified = isVerified;
    }

    public StringFilter getLastLoginIp() {
        return lastLoginIp;
    }

    public Optional<StringFilter> optionalLastLoginIp() {
        return Optional.ofNullable(lastLoginIp);
    }

    public StringFilter lastLoginIp() {
        if (lastLoginIp == null) {
            setLastLoginIp(new StringFilter());
        }
        return lastLoginIp;
    }

    public void setLastLoginIp(StringFilter lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public StringFilter getLastUserAgent() {
        return lastUserAgent;
    }

    public Optional<StringFilter> optionalLastUserAgent() {
        return Optional.ofNullable(lastUserAgent);
    }

    public StringFilter lastUserAgent() {
        if (lastUserAgent == null) {
            setLastUserAgent(new StringFilter());
        }
        return lastUserAgent;
    }

    public void setLastUserAgent(StringFilter lastUserAgent) {
        this.lastUserAgent = lastUserAgent;
    }

    public InstantFilter getLastLoginDate() {
        return lastLoginDate;
    }

    public Optional<InstantFilter> optionalLastLoginDate() {
        return Optional.ofNullable(lastLoginDate);
    }

    public InstantFilter lastLoginDate() {
        if (lastLoginDate == null) {
            setLastLoginDate(new InstantFilter());
        }
        return lastLoginDate;
    }

    public void setLastLoginDate(InstantFilter lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public StringFilter getInternalUserId() {
        return internalUserId;
    }

    public Optional<StringFilter> optionalInternalUserId() {
        return Optional.ofNullable(internalUserId);
    }

    public StringFilter internalUserId() {
        if (internalUserId == null) {
            setInternalUserId(new StringFilter());
        }
        return internalUserId;
    }

    public void setInternalUserId(StringFilter internalUserId) {
        this.internalUserId = internalUserId;
    }

    public LongFilter getSpecializationsId() {
        return specializationsId;
    }

    public Optional<LongFilter> optionalSpecializationsId() {
        return Optional.ofNullable(specializationsId);
    }

    public LongFilter specializationsId() {
        if (specializationsId == null) {
            setSpecializationsId(new LongFilter());
        }
        return specializationsId;
    }

    public void setSpecializationsId(LongFilter specializationsId) {
        this.specializationsId = specializationsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DoctorProfileCriteria that = (DoctorProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(officeAddress, that.officeAddress) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(inpeCode, that.inpeCode) &&
            Objects.equals(isVerified, that.isVerified) &&
            Objects.equals(lastLoginIp, that.lastLoginIp) &&
            Objects.equals(lastUserAgent, that.lastUserAgent) &&
            Objects.equals(lastLoginDate, that.lastLoginDate) &&
            Objects.equals(internalUserId, that.internalUserId) &&
            Objects.equals(specializationsId, that.specializationsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            phoneNumber,
            officeAddress,
            latitude,
            longitude,
            inpeCode,
            isVerified,
            lastLoginIp,
            lastUserAgent,
            lastLoginDate,
            internalUserId,
            specializationsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalOfficeAddress().map(f -> "officeAddress=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalInpeCode().map(f -> "inpeCode=" + f + ", ").orElse("") +
            optionalIsVerified().map(f -> "isVerified=" + f + ", ").orElse("") +
            optionalLastLoginIp().map(f -> "lastLoginIp=" + f + ", ").orElse("") +
            optionalLastUserAgent().map(f -> "lastUserAgent=" + f + ", ").orElse("") +
            optionalLastLoginDate().map(f -> "lastLoginDate=" + f + ", ").orElse("") +
            optionalInternalUserId().map(f -> "internalUserId=" + f + ", ").orElse("") +
            optionalSpecializationsId().map(f -> "specializationsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
