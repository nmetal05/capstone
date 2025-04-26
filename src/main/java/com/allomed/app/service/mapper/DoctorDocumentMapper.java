package com.allomed.app.service.mapper;

import com.allomed.app.domain.DoctorDocument;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.service.dto.DoctorDocumentDTO;
import com.allomed.app.service.dto.DoctorProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorDocument} and its DTO {@link DoctorDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorDocumentMapper extends EntityMapper<DoctorDocumentDTO, DoctorDocument> {
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorProfileId")
    DoctorDocumentDTO toDto(DoctorDocument s);

    @Named("doctorProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DoctorProfileDTO toDtoDoctorProfileId(DoctorProfile doctorProfile);
}
