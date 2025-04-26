package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorProfileDTO.class);
        DoctorProfileDTO doctorProfileDTO1 = new DoctorProfileDTO();
        doctorProfileDTO1.setId("id1");
        DoctorProfileDTO doctorProfileDTO2 = new DoctorProfileDTO();
        assertThat(doctorProfileDTO1).isNotEqualTo(doctorProfileDTO2);
        doctorProfileDTO2.setId(doctorProfileDTO1.getId());
        assertThat(doctorProfileDTO1).isEqualTo(doctorProfileDTO2);
        doctorProfileDTO2.setId("id2");
        assertThat(doctorProfileDTO1).isNotEqualTo(doctorProfileDTO2);
        doctorProfileDTO1.setId(null);
        assertThat(doctorProfileDTO1).isNotEqualTo(doctorProfileDTO2);
    }
}
