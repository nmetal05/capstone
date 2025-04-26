package com.allomed.app.service.mapper;

import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.Specialization;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.dto.SpecializationDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Specialization} and its DTO {@link SpecializationDTO}.
 */
@Mapper(componentModel = "spring")
public interface SpecializationMapper extends EntityMapper<SpecializationDTO, Specialization> {
    @Mapping(target = "doctorProfiles", source = "doctorProfiles", qualifiedByName = "doctorProfileIdSet")
    SpecializationDTO toDto(Specialization s);

    @Mapping(target = "doctorProfiles", ignore = true)
    @Mapping(target = "removeDoctorProfiles", ignore = true)
    Specialization toEntity(SpecializationDTO specializationDTO);

    @Named("doctorProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DoctorProfileDTO toDtoDoctorProfileId(DoctorProfile doctorProfile);

    @Named("doctorProfileIdSet")
    default Set<DoctorProfileDTO> toDtoDoctorProfileIdSet(Set<DoctorProfile> doctorProfile) {
        return doctorProfile.stream().map(this::toDtoDoctorProfileId).collect(Collectors.toSet());
    }
}
