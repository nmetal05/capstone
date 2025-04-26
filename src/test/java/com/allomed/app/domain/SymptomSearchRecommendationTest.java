package com.allomed.app.domain;

import static com.allomed.app.domain.SpecializationTestSamples.*;
import static com.allomed.app.domain.SymptomSearchRecommendationTestSamples.*;
import static com.allomed.app.domain.SymptomSearchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SymptomSearchRecommendationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SymptomSearchRecommendation.class);
        SymptomSearchRecommendation symptomSearchRecommendation1 = getSymptomSearchRecommendationSample1();
        SymptomSearchRecommendation symptomSearchRecommendation2 = new SymptomSearchRecommendation();
        assertThat(symptomSearchRecommendation1).isNotEqualTo(symptomSearchRecommendation2);

        symptomSearchRecommendation2.setId(symptomSearchRecommendation1.getId());
        assertThat(symptomSearchRecommendation1).isEqualTo(symptomSearchRecommendation2);

        symptomSearchRecommendation2 = getSymptomSearchRecommendationSample2();
        assertThat(symptomSearchRecommendation1).isNotEqualTo(symptomSearchRecommendation2);
    }

    @Test
    void searchTest() {
        SymptomSearchRecommendation symptomSearchRecommendation = getSymptomSearchRecommendationRandomSampleGenerator();
        SymptomSearch symptomSearchBack = getSymptomSearchRandomSampleGenerator();

        symptomSearchRecommendation.setSearch(symptomSearchBack);
        assertThat(symptomSearchRecommendation.getSearch()).isEqualTo(symptomSearchBack);

        symptomSearchRecommendation.search(null);
        assertThat(symptomSearchRecommendation.getSearch()).isNull();
    }

    @Test
    void specializationTest() {
        SymptomSearchRecommendation symptomSearchRecommendation = getSymptomSearchRecommendationRandomSampleGenerator();
        Specialization specializationBack = getSpecializationRandomSampleGenerator();

        symptomSearchRecommendation.setSpecialization(specializationBack);
        assertThat(symptomSearchRecommendation.getSpecialization()).isEqualTo(specializationBack);

        symptomSearchRecommendation.specialization(null);
        assertThat(symptomSearchRecommendation.getSpecialization()).isNull();
    }
}
