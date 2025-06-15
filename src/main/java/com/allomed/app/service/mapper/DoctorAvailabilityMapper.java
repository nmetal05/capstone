package com.allomed.app.service.mapper;

import com.allomed.app.domain.DoctorAvailability;
import com.allomed.app.service.dto.DoctorAvailabilityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DoctorAvailability} and its DTO {@link DoctorAvailabilityDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorAvailabilityMapper extends EntityMapper<DoctorAvailabilityDTO, DoctorAvailability> {
    @Mapping(target = "doctorId", source = "doctor.id")
    DoctorAvailabilityDTO toDto(DoctorAvailability s);

    @Mapping(target = "doctor.id", source = "doctorId")
    DoctorAvailability toEntity(DoctorAvailabilityDTO doctorAvailabilityDTO);
}
