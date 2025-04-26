package com.allomed.app.domain;

import static com.allomed.app.domain.AppUserProfileTestSamples.*;
import static com.allomed.app.domain.DoctorProfileTestSamples.*;
import static com.allomed.app.domain.DoctorViewHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DoctorViewHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorViewHistory.class);
        DoctorViewHistory doctorViewHistory1 = getDoctorViewHistorySample1();
        DoctorViewHistory doctorViewHistory2 = new DoctorViewHistory();
        assertThat(doctorViewHistory1).isNotEqualTo(doctorViewHistory2);

        doctorViewHistory2.setId(doctorViewHistory1.getId());
        assertThat(doctorViewHistory1).isEqualTo(doctorViewHistory2);

        doctorViewHistory2 = getDoctorViewHistorySample2();
        assertThat(doctorViewHistory1).isNotEqualTo(doctorViewHistory2);
    }

    @Test
    void userTest() {
        DoctorViewHistory doctorViewHistory = getDoctorViewHistoryRandomSampleGenerator();
        AppUserProfile appUserProfileBack = getAppUserProfileRandomSampleGenerator();

        doctorViewHistory.setUser(appUserProfileBack);
        assertThat(doctorViewHistory.getUser()).isEqualTo(appUserProfileBack);

        doctorViewHistory.user(null);
        assertThat(doctorViewHistory.getUser()).isNull();
    }

    @Test
    void doctorTest() {
        DoctorViewHistory doctorViewHistory = getDoctorViewHistoryRandomSampleGenerator();
        DoctorProfile doctorProfileBack = getDoctorProfileRandomSampleGenerator();

        doctorViewHistory.setDoctor(doctorProfileBack);
        assertThat(doctorViewHistory.getDoctor()).isEqualTo(doctorProfileBack);

        doctorViewHistory.doctor(null);
        assertThat(doctorViewHistory.getDoctor()).isNull();
    }
}
