package com.allomed.app.domain;

import com.allomed.app.domain.enumeration.DocumentType;
import com.allomed.app.domain.enumeration.VerificationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DoctorDocument.
 */
@Entity
@Table(name = "doctor_document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "doctordocument")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private DocumentType type;

    @NotNull
    @Column(name = "file_name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String fileName;

    @Lob
    @Column(name = "file_content", nullable = false)
    private byte[] fileContent;

    @NotNull
    @Column(name = "file_content_content_type", nullable = false)
    private String fileContentContentType;

    @NotNull
    @Column(name = "upload_date", nullable = false)
    private Instant uploadDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private VerificationStatus verificationStatus;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "internalUser", "specializations" }, allowSetters = true)
    private DoctorProfile doctor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DoctorDocument id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentType getType() {
        return this.type;
    }

    public DoctorDocument type(DocumentType type) {
        this.setType(type);
        return this;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getFileName() {
        return this.fileName;
    }

    public DoctorDocument fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContent() {
        return this.fileContent;
    }

    public DoctorDocument fileContent(byte[] fileContent) {
        this.setFileContent(fileContent);
        return this;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileContentContentType() {
        return this.fileContentContentType;
    }

    public DoctorDocument fileContentContentType(String fileContentContentType) {
        this.fileContentContentType = fileContentContentType;
        return this;
    }

    public void setFileContentContentType(String fileContentContentType) {
        this.fileContentContentType = fileContentContentType;
    }

    public Instant getUploadDate() {
        return this.uploadDate;
    }

    public DoctorDocument uploadDate(Instant uploadDate) {
        this.setUploadDate(uploadDate);
        return this;
    }

    public void setUploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
    }

    public VerificationStatus getVerificationStatus() {
        return this.verificationStatus;
    }

    public DoctorDocument verificationStatus(VerificationStatus verificationStatus) {
        this.setVerificationStatus(verificationStatus);
        return this;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public DoctorProfile getDoctor() {
        return this.doctor;
    }

    public void setDoctor(DoctorProfile doctorProfile) {
        this.doctor = doctorProfile;
    }

    public DoctorDocument doctor(DoctorProfile doctorProfile) {
        this.setDoctor(doctorProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorDocument)) {
            return false;
        }
        return getId() != null && getId().equals(((DoctorDocument) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorDocument{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", fileContent='" + getFileContent() + "'" +
            ", fileContentContentType='" + getFileContentContentType() + "'" +
            ", uploadDate='" + getUploadDate() + "'" +
            ", verificationStatus='" + getVerificationStatus() + "'" +
            "}";
    }
}
