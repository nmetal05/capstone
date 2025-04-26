package com.allomed.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A DoctorProfile.
 */
@Entity
@Table(name = "doctor_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@org.springframework.data.elasticsearch.annotations.Document(indexName = "doctorprofile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorProfile implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @NotNull
    @Column(name = "office_address", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String officeAddress;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @Column(name = "inpe_code", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String inpeCode;

    @NotNull
    @Column(name = "is_verified", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isVerified;

    @Column(name = "last_login_ip")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastLoginIp;

    @Column(name = "last_user_agent")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastUserAgent;

    @Column(name = "last_login_date")
    private Instant lastLoginDate;

    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User internalUser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_doctor_profile__specializations",
        joinColumns = @JoinColumn(name = "doctor_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "specializations_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "doctorProfiles" }, allowSetters = true)
    private Set<Specialization> specializations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public DoctorProfile id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public DoctorProfile phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOfficeAddress() {
        return this.officeAddress;
    }

    public DoctorProfile officeAddress(String officeAddress) {
        this.setOfficeAddress(officeAddress);
        return this;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public DoctorProfile latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public DoctorProfile longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getInpeCode() {
        return this.inpeCode;
    }

    public DoctorProfile inpeCode(String inpeCode) {
        this.setInpeCode(inpeCode);
        return this;
    }

    public void setInpeCode(String inpeCode) {
        this.inpeCode = inpeCode;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public DoctorProfile isVerified(Boolean isVerified) {
        this.setIsVerified(isVerified);
        return this;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getLastLoginIp() {
        return this.lastLoginIp;
    }

    public DoctorProfile lastLoginIp(String lastLoginIp) {
        this.setLastLoginIp(lastLoginIp);
        return this;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastUserAgent() {
        return this.lastUserAgent;
    }

    public DoctorProfile lastUserAgent(String lastUserAgent) {
        this.setLastUserAgent(lastUserAgent);
        return this;
    }

    public void setLastUserAgent(String lastUserAgent) {
        this.lastUserAgent = lastUserAgent;
    }

    public Instant getLastLoginDate() {
        return this.lastLoginDate;
    }

    public DoctorProfile lastLoginDate(Instant lastLoginDate) {
        this.setLastLoginDate(lastLoginDate);
        return this;
    }

    public void setLastLoginDate(Instant lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @org.springframework.data.annotation.Transient
    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public DoctorProfile setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public DoctorProfile internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public Set<Specialization> getSpecializations() {
        return this.specializations;
    }

    public void setSpecializations(Set<Specialization> specializations) {
        this.specializations = specializations;
    }

    public DoctorProfile specializations(Set<Specialization> specializations) {
        this.setSpecializations(specializations);
        return this;
    }

    public DoctorProfile addSpecializations(Specialization specialization) {
        this.specializations.add(specialization);
        return this;
    }

    public DoctorProfile removeSpecializations(Specialization specialization) {
        this.specializations.remove(specialization);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((DoctorProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorProfile{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", officeAddress='" + getOfficeAddress() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", inpeCode='" + getInpeCode() + "'" +
            ", isVerified='" + getIsVerified() + "'" +
            ", lastLoginIp='" + getLastLoginIp() + "'" +
            ", lastUserAgent='" + getLastUserAgent() + "'" +
            ", lastLoginDate='" + getLastLoginDate() + "'" +
            "}";
    }
}
