package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorViewHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorViewHistoryDTO.class);
        DoctorViewHistoryDTO doctorViewHistoryDTO1 = new DoctorViewHistoryDTO();
        doctorViewHistoryDTO1.setId(1L);
        DoctorViewHistoryDTO doctorViewHistoryDTO2 = new DoctorViewHistoryDTO();
        assertThat(doctorViewHistoryDTO1).isNotEqualTo(doctorViewHistoryDTO2);
        doctorViewHistoryDTO2.setId(doctorViewHistoryDTO1.getId());
        assertThat(doctorViewHistoryDTO1).isEqualTo(doctorViewHistoryDTO2);
        doctorViewHistoryDTO2.setId(2L);
        assertThat(doctorViewHistoryDTO1).isNotEqualTo(doctorViewHistoryDTO2);
        doctorViewHistoryDTO1.setId(null);
        assertThat(doctorViewHistoryDTO1).isNotEqualTo(doctorViewHistoryDTO2);
    }
}
