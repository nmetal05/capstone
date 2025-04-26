package com.allomed.app.domain;

import static com.allomed.app.domain.DoctorDocumentTestSamples.*;
import static com.allomed.app.domain.DoctorProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorDocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorDocument.class);
        DoctorDocument doctorDocument1 = getDoctorDocumentSample1();
        DoctorDocument doctorDocument2 = new DoctorDocument();
        assertThat(doctorDocument1).isNotEqualTo(doctorDocument2);

        doctorDocument2.setId(doctorDocument1.getId());
        assertThat(doctorDocument1).isEqualTo(doctorDocument2);

        doctorDocument2 = getDoctorDocumentSample2();
        assertThat(doctorDocument1).isNotEqualTo(doctorDocument2);
    }

    @Test
    void doctorTest() {
        DoctorDocument doctorDocument = getDoctorDocumentRandomSampleGenerator();
        DoctorProfile doctorProfileBack = getDoctorProfileRandomSampleGenerator();

        doctorDocument.setDoctor(doctorProfileBack);
        assertThat(doctorDocument.getDoctor()).isEqualTo(doctorProfileBack);

        doctorDocument.doctor(null);
        assertThat(doctorDocument.getDoctor()).isNull();
    }
}
