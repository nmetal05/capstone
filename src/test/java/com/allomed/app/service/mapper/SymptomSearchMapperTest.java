package com.allomed.app.service.mapper;

import static com.allomed.app.domain.SymptomSearchAsserts.*;
import static com.allomed.app.domain.SymptomSearchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SymptomSearchMapperTest {

    private SymptomSearchMapper symptomSearchMapper;

    @BeforeEach
    void setUp() {
        symptomSearchMapper = new SymptomSearchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSymptomSearchSample1();
        var actual = symptomSearchMapper.toEntity(symptomSearchMapper.toDto(expected));
        assertSymptomSearchAllPropertiesEquals(expected, actual);
    }
}
