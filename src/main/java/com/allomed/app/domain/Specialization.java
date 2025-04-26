package com.allomed.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Specialization.
 */
@Entity
@Table(name = "specialization")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "specialization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Specialization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "specializations")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "internalUser", "specializations" }, allowSetters = true)
    private Set<DoctorProfile> doctorProfiles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Specialization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Specialization name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Specialization description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<DoctorProfile> getDoctorProfiles() {
        return this.doctorProfiles;
    }

    public void setDoctorProfiles(Set<DoctorProfile> doctorProfiles) {
        if (this.doctorProfiles != null) {
            this.doctorProfiles.forEach(i -> i.removeSpecializations(this));
        }
        if (doctorProfiles != null) {
            doctorProfiles.forEach(i -> i.addSpecializations(this));
        }
        this.doctorProfiles = doctorProfiles;
    }

    public Specialization doctorProfiles(Set<DoctorProfile> doctorProfiles) {
        this.setDoctorProfiles(doctorProfiles);
        return this;
    }

    public Specialization addDoctorProfiles(DoctorProfile doctorProfile) {
        this.doctorProfiles.add(doctorProfile);
        doctorProfile.getSpecializations().add(this);
        return this;
    }

    public Specialization removeDoctorProfiles(DoctorProfile doctorProfile) {
        this.doctorProfiles.remove(doctorProfile);
        doctorProfile.getSpecializations().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Specialization)) {
            return false;
        }
        return getId() != null && getId().equals(((Specialization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Specialization{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
