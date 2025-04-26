package com.allomed.app.service.mapper;

import static com.allomed.app.domain.DoctorDocumentAsserts.*;
import static com.allomed.app.domain.DoctorDocumentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoctorDocumentMapperTest {

    private DoctorDocumentMapper doctorDocumentMapper;

    @BeforeEach
    void setUp() {
        doctorDocumentMapper = new DoctorDocumentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDoctorDocumentSample1();
        var actual = doctorDocumentMapper.toEntity(doctorDocumentMapper.toDto(expected));
        assertDoctorDocumentAllPropertiesEquals(expected, actual);
    }
}
