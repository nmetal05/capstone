package com.allomed.app.service.mapper;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.User;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUserProfile} and its DTO {@link AppUserProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppUserProfileMapper extends EntityMapper<AppUserProfileDTO, AppUserProfile> {
    @Mapping(target = "internalUser", source = "internalUser", qualifiedByName = "userLogin")
    AppUserProfileDTO toDto(AppUserProfile s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
