package com.allomed.app.service.mapper;

import static com.allomed.app.domain.SpecializationAsserts.*;
import static com.allomed.app.domain.SpecializationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecializationMapperTest {

    private SpecializationMapper specializationMapper;

    @BeforeEach
    void setUp() {
        specializationMapper = new SpecializationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSpecializationSample1();
        var actual = specializationMapper.toEntity(specializationMapper.toDto(expected));
        assertSpecializationAllPropertiesEquals(expected, actual);
    }
}
