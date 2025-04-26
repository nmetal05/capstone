package com.allomed.app.service.mapper;

import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.Specialization;
import com.allomed.app.domain.User;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.dto.SpecializationDTO;
import com.allomed.app.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorProfile} and its DTO {@link DoctorProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorProfileMapper extends EntityMapper<DoctorProfileDTO, DoctorProfile> {
    @Mapping(target = "internalUser", source = "internalUser", qualifiedByName = "userLogin")
    @Mapping(target = "specializations", source = "specializations", qualifiedByName = "specializationNameSet")
    DoctorProfileDTO toDto(DoctorProfile s);

    @Mapping(target = "removeSpecializations", ignore = true)
    DoctorProfile toEntity(DoctorProfileDTO doctorProfileDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("specializationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SpecializationDTO toDtoSpecializationName(Specialization specialization);

    @Named("specializationNameSet")
    default Set<SpecializationDTO> toDtoSpecializationNameSet(Set<Specialization> specialization) {
        return specialization.stream().map(this::toDtoSpecializationName).collect(Collectors.toSet());
    }
}
