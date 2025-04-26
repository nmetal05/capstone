package com.allomed.app.domain;

import static com.allomed.app.domain.DoctorProfileTestSamples.*;
import static com.allomed.app.domain.SpecializationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpecializationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Specialization.class);
        Specialization specialization1 = getSpecializationSample1();
        Specialization specialization2 = new Specialization();
        assertThat(specialization1).isNotEqualTo(specialization2);

        specialization2.setId(specialization1.getId());
        assertThat(specialization1).isEqualTo(specialization2);

        specialization2 = getSpecializationSample2();
        assertThat(specialization1).isNotEqualTo(specialization2);
    }

    @Test
    void doctorProfilesTest() {
        Specialization specialization = getSpecializationRandomSampleGenerator();
        DoctorProfile doctorProfileBack = getDoctorProfileRandomSampleGenerator();

        specialization.addDoctorProfiles(doctorProfileBack);
        assertThat(specialization.getDoctorProfiles()).containsOnly(doctorProfileBack);
        assertThat(doctorProfileBack.getSpecializations()).containsOnly(specialization);

        specialization.removeDoctorProfiles(doctorProfileBack);
        assertThat(specialization.getDoctorProfiles()).doesNotContain(doctorProfileBack);
        assertThat(doctorProfileBack.getSpecializations()).doesNotContain(specialization);

        specialization.doctorProfiles(new HashSet<>(Set.of(doctorProfileBack)));
        assertThat(specialization.getDoctorProfiles()).containsOnly(doctorProfileBack);
        assertThat(doctorProfileBack.getSpecializations()).containsOnly(specialization);

        specialization.setDoctorProfiles(new HashSet<>());
        assertThat(specialization.getDoctorProfiles()).doesNotContain(doctorProfileBack);
        assertThat(doctorProfileBack.getSpecializations()).doesNotContain(specialization);
    }
}
