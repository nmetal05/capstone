package com.allomed.app.service.mapper;

import com.allomed.app.domain.DoctorDocument;
import com.allomed.app.service.dto.DoctorDocumentDTO;
import org.springframework.stereotype.Component;

/**
 * Utility class for DoctorDocument mapping with eager relationships
 */
@Component
public class DoctorDocumentMapperUtil {

    private final DoctorDocumentMapper doctorDocumentMapper;

    public DoctorDocumentMapperUtil(DoctorDocumentMapper doctorDocumentMapper) {
        this.doctorDocumentMapper = doctorDocumentMapper;
    }

    /**
     * Convert DoctorDocument entity to DTO with eager relationships loaded
     *
     * @param doctorDocument the entity to convert
     * @return the DTO with eager relationships
     */
    public DoctorDocumentDTO toDtoWithEagerRelationships(DoctorDocument doctorDocument) {
        if (doctorDocument == null) {
            return null;
        }

        DoctorDocumentDTO dto = new DoctorDocumentDTO();
        dto.setId(doctorDocument.getId());
        dto.setType(doctorDocument.getType());
        dto.setFileName(doctorDocument.getFileName());
        dto.setFileContent(doctorDocument.getFileContent());
        dto.setFileContentContentType(doctorDocument.getFileContentContentType());
        dto.setUploadDate(doctorDocument.getUploadDate());
        dto.setVerificationStatus(doctorDocument.getVerificationStatus());
        dto.setDoctor(doctorDocumentMapper.toDtoDoctorProfileWithUser(doctorDocument.getDoctor()));

        return dto;
    }
}
