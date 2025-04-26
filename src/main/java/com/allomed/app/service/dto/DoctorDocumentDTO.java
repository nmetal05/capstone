package com.allomed.app.service.dto;

import com.allomed.app.domain.enumeration.DocumentType;
import com.allomed.app.domain.enumeration.VerificationStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.allomed.app.domain.DoctorDocument} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorDocumentDTO implements Serializable {

    private Long id;

    @NotNull
    private DocumentType type;

    @NotNull
    private String fileName;

    @Lob
    private byte[] fileContent;

    private String fileContentContentType;

    @NotNull
    private Instant uploadDate;

    @NotNull
    private VerificationStatus verificationStatus;

    @NotNull
    private DoctorProfileDTO doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileContentContentType() {
        return fileContentContentType;
    }

    public void setFileContentContentType(String fileContentContentType) {
        this.fileContentContentType = fileContentContentType;
    }

    public Instant getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Instant uploadDate) {
        this.uploadDate = uploadDate;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public DoctorProfileDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorProfileDTO doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorDocumentDTO)) {
            return false;
        }

        DoctorDocumentDTO doctorDocumentDTO = (DoctorDocumentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, doctorDocumentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorDocumentDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", fileContent='" + getFileContent() + "'" +
            ", uploadDate='" + getUploadDate() + "'" +
            ", verificationStatus='" + getVerificationStatus() + "'" +
            ", doctor=" + getDoctor() +
            "}";
    }
}
