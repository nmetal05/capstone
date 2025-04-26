package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SymptomSearchRecommendationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SymptomSearchRecommendationDTO.class);
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO1 = new SymptomSearchRecommendationDTO();
        symptomSearchRecommendationDTO1.setId(1L);
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO2 = new SymptomSearchRecommendationDTO();
        assertThat(symptomSearchRecommendationDTO1).isNotEqualTo(symptomSearchRecommendationDTO2);
        symptomSearchRecommendationDTO2.setId(symptomSearchRecommendationDTO1.getId());
        assertThat(symptomSearchRecommendationDTO1).isEqualTo(symptomSearchRecommendationDTO2);
        symptomSearchRecommendationDTO2.setId(2L);
        assertThat(symptomSearchRecommendationDTO1).isNotEqualTo(symptomSearchRecommendationDTO2);
        symptomSearchRecommendationDTO1.setId(null);
        assertThat(symptomSearchRecommendationDTO1).isNotEqualTo(symptomSearchRecommendationDTO2);
    }
}
