package com.allomed.app.service.mapper;

import static com.allomed.app.domain.DoctorProfileAsserts.*;
import static com.allomed.app.domain.DoctorProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoctorProfileMapperTest {

    private DoctorProfileMapper doctorProfileMapper;

    @BeforeEach
    void setUp() {
        doctorProfileMapper = new DoctorProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDoctorProfileSample1();
        var actual = doctorProfileMapper.toEntity(doctorProfileMapper.toDto(expected));
        assertDoctorProfileAllPropertiesEquals(expected, actual);
    }
}
