package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SymptomSearchDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SymptomSearchDTO.class);
        SymptomSearchDTO symptomSearchDTO1 = new SymptomSearchDTO();
        symptomSearchDTO1.setId(1L);
        SymptomSearchDTO symptomSearchDTO2 = new SymptomSearchDTO();
        assertThat(symptomSearchDTO1).isNotEqualTo(symptomSearchDTO2);
        symptomSearchDTO2.setId(symptomSearchDTO1.getId());
        assertThat(symptomSearchDTO1).isEqualTo(symptomSearchDTO2);
        symptomSearchDTO2.setId(2L);
        assertThat(symptomSearchDTO1).isNotEqualTo(symptomSearchDTO2);
        symptomSearchDTO1.setId(null);
        assertThat(symptomSearchDTO1).isNotEqualTo(symptomSearchDTO2);
    }
}
