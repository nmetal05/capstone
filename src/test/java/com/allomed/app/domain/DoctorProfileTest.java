package com.allomed.app.domain;

import static com.allomed.app.domain.DoctorProfileTestSamples.*;
import static com.allomed.app.domain.SpecializationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DoctorProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorProfile.class);
        DoctorProfile doctorProfile1 = getDoctorProfileSample1();
        DoctorProfile doctorProfile2 = new DoctorProfile();
        assertThat(doctorProfile1).isNotEqualTo(doctorProfile2);

        doctorProfile2.setId(doctorProfile1.getId());
        assertThat(doctorProfile1).isEqualTo(doctorProfile2);

        doctorProfile2 = getDoctorProfileSample2();
        assertThat(doctorProfile1).isNotEqualTo(doctorProfile2);
    }

    @Test
    void specializationsTest() {
        DoctorProfile doctorProfile = getDoctorProfileRandomSampleGenerator();
        Specialization specializationBack = getSpecializationRandomSampleGenerator();

        doctorProfile.addSpecializations(specializationBack);
        assertThat(doctorProfile.getSpecializations()).containsOnly(specializationBack);

        doctorProfile.removeSpecializations(specializationBack);
        assertThat(doctorProfile.getSpecializations()).doesNotContain(specializationBack);

        doctorProfile.specializations(new HashSet<>(Set.of(specializationBack)));
        assertThat(doctorProfile.getSpecializations()).containsOnly(specializationBack);

        doctorProfile.setSpecializations(new HashSet<>());
        assertThat(doctorProfile.getSpecializations()).doesNotContain(specializationBack);
    }
}
