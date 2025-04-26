package com.allomed.app.service.mapper;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.DoctorViewHistory;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorViewHistory} and its DTO {@link DoctorViewHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorViewHistoryMapper extends EntityMapper<DoctorViewHistoryDTO, DoctorViewHistory> {
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserProfileId")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorProfileId")
    DoctorViewHistoryDTO toDto(DoctorViewHistory s);

    @Named("appUserProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserProfileDTO toDtoAppUserProfileId(AppUserProfile appUserProfile);

    @Named("doctorProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DoctorProfileDTO toDtoDoctorProfileId(DoctorProfile doctorProfile);
}
