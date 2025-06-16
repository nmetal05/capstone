package com.allomed.app.service.mapper;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.DoctorViewHistory;
import com.allomed.app.domain.Specialization;
import com.allomed.app.domain.User;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import com.allomed.app.service.dto.SpecializationDTO;
import com.allomed.app.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorViewHistory} and its DTO {@link DoctorViewHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorViewHistoryMapper extends EntityMapper<DoctorViewHistoryDTO, DoctorViewHistory> {
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserProfileId")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorProfileId")
    @Mapping(target = "googleDoctorData", source = "googleDoctorData")
    DoctorViewHistoryDTO toDto(DoctorViewHistory s);

    @Named("appUserProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserProfileDTO toDtoAppUserProfileId(AppUserProfile appUserProfile);

    @Named("doctorProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "officeAddress", source = "officeAddress")
    @Mapping(target = "isVerified", source = "isVerified")
    @Mapping(target = "internalUser", source = "internalUser", qualifiedByName = "userForHistory")
    @Mapping(target = "specializations", source = "specializations", qualifiedByName = "specializationSetForHistory")
    DoctorProfileDTO toDtoDoctorProfileId(DoctorProfile doctorProfile);

    @Named("userForHistory")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserForHistory(User user);

    @Named("specializationForHistory")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SpecializationDTO toDtoSpecializationForHistory(Specialization specialization);

    @Named("specializationSetForHistory")
    default Set<SpecializationDTO> toDtoSpecializationSetForHistory(Set<Specialization> specializations) {
        return specializations.stream().map(this::toDtoSpecializationForHistory).collect(Collectors.toSet());
    }

    // Override the default entity mapping to ignore problematic fields
    @Mapping(target = "removeSpecializations", ignore = true)
    DoctorProfile toEntity(DoctorProfileDTO doctorProfileDTO);
}
