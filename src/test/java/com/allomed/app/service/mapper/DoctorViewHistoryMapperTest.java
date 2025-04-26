package com.allomed.app.service.mapper;

import static com.allomed.app.domain.DoctorViewHistoryAsserts.*;
import static com.allomed.app.domain.DoctorViewHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoctorViewHistoryMapperTest {

    private DoctorViewHistoryMapper doctorViewHistoryMapper;

    @BeforeEach
    void setUp() {
        doctorViewHistoryMapper = new DoctorViewHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDoctorViewHistorySample1();
        var actual = doctorViewHistoryMapper.toEntity(doctorViewHistoryMapper.toDto(expected));
        assertDoctorViewHistoryAllPropertiesEquals(expected, actual);
    }
}
