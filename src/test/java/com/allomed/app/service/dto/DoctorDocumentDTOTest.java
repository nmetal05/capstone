package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorDocumentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorDocumentDTO.class);
        DoctorDocumentDTO doctorDocumentDTO1 = new DoctorDocumentDTO();
        doctorDocumentDTO1.setId(1L);
        DoctorDocumentDTO doctorDocumentDTO2 = new DoctorDocumentDTO();
        assertThat(doctorDocumentDTO1).isNotEqualTo(doctorDocumentDTO2);
        doctorDocumentDTO2.setId(doctorDocumentDTO1.getId());
        assertThat(doctorDocumentDTO1).isEqualTo(doctorDocumentDTO2);
        doctorDocumentDTO2.setId(2L);
        assertThat(doctorDocumentDTO1).isNotEqualTo(doctorDocumentDTO2);
        doctorDocumentDTO1.setId(null);
        assertThat(doctorDocumentDTO1).isNotEqualTo(doctorDocumentDTO2);
    }
}
