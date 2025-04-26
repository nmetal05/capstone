package com.allomed.app.service.mapper;

import com.allomed.app.domain.GuestSession;
import com.allomed.app.service.dto.GuestSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GuestSession} and its DTO {@link GuestSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface GuestSessionMapper extends EntityMapper<GuestSessionDTO, GuestSession> {}
