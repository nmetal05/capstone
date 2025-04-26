package com.allomed.app.service.mapper;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.GuestSession;
import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.dto.GuestSessionDTO;
import com.allomed.app.service.dto.SymptomSearchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SymptomSearch} and its DTO {@link SymptomSearchDTO}.
 */
@Mapper(componentModel = "spring")
public interface SymptomSearchMapper extends EntityMapper<SymptomSearchDTO, SymptomSearch> {
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserProfileId")
    @Mapping(target = "guestSession", source = "guestSession", qualifiedByName = "guestSessionId")
    SymptomSearchDTO toDto(SymptomSearch s);

    @Named("appUserProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserProfileDTO toDtoAppUserProfileId(AppUserProfile appUserProfile);

    @Named("guestSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GuestSessionDTO toDtoGuestSessionId(GuestSession guestSession);
}
