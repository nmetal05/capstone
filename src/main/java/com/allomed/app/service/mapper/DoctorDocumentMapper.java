package com.allomed.app.service.mapper;

import com.allomed.app.domain.DoctorDocument;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.User;
import com.allomed.app.service.dto.DoctorDocumentDTO;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorDocument} and its DTO {@link DoctorDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorDocumentMapper extends EntityMapper<DoctorDocumentDTO, DoctorDocument> {
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorProfileId")
    DoctorDocumentDTO toDto(DoctorDocument s);

    DoctorDocument toEntity(DoctorDocumentDTO doctorDocumentDTO);

    @Named("doctorProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DoctorProfileDTO toDtoDoctorProfileId(DoctorProfile doctorProfile);

    @Named("doctorProfileWithUser")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "internalUser", source = "internalUser", qualifiedByName = "userBasic")
    DoctorProfileDTO toDtoDoctorProfileWithUser(DoctorProfile doctorProfile);

    @Named("userBasic")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserBasic(User user);
}
