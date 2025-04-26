package com.allomed.app.domain;

import static com.allomed.app.domain.AppUserProfileTestSamples.*;
import static com.allomed.app.domain.GuestSessionTestSamples.*;
import static com.allomed.app.domain.SymptomSearchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SymptomSearchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SymptomSearch.class);
        SymptomSearch symptomSearch1 = getSymptomSearchSample1();
        SymptomSearch symptomSearch2 = new SymptomSearch();
        assertThat(symptomSearch1).isNotEqualTo(symptomSearch2);

        symptomSearch2.setId(symptomSearch1.getId());
        assertThat(symptomSearch1).isEqualTo(symptomSearch2);

        symptomSearch2 = getSymptomSearchSample2();
        assertThat(symptomSearch1).isNotEqualTo(symptomSearch2);
    }

    @Test
    void userTest() {
        SymptomSearch symptomSearch = getSymptomSearchRandomSampleGenerator();
        AppUserProfile appUserProfileBack = getAppUserProfileRandomSampleGenerator();

        symptomSearch.setUser(appUserProfileBack);
        assertThat(symptomSearch.getUser()).isEqualTo(appUserProfileBack);

        symptomSearch.user(null);
        assertThat(symptomSearch.getUser()).isNull();
    }

    @Test
    void guestSessionTest() {
        SymptomSearch symptomSearch = getSymptomSearchRandomSampleGenerator();
        GuestSession guestSessionBack = getGuestSessionRandomSampleGenerator();

        symptomSearch.setGuestSession(guestSessionBack);
        assertThat(symptomSearch.getGuestSession()).isEqualTo(guestSessionBack);

        symptomSearch.guestSession(null);
        assertThat(symptomSearch.getGuestSession()).isNull();
    }
}
