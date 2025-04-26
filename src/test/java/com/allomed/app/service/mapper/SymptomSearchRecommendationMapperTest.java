package com.allomed.app.service.mapper;

import static com.allomed.app.domain.SymptomSearchRecommendationAsserts.*;
import static com.allomed.app.domain.SymptomSearchRecommendationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SymptomSearchRecommendationMapperTest {

    private SymptomSearchRecommendationMapper symptomSearchRecommendationMapper;

    @BeforeEach
    void setUp() {
        symptomSearchRecommendationMapper = new SymptomSearchRecommendationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSymptomSearchRecommendationSample1();
        var actual = symptomSearchRecommendationMapper.toEntity(symptomSearchRecommendationMapper.toDto(expected));
        assertSymptomSearchRecommendationAllPropertiesEquals(expected, actual);
    }
}
