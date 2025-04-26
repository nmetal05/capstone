package com.allomed.app.domain;

import static com.allomed.app.domain.AppUserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppUserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUserProfile.class);
        AppUserProfile appUserProfile1 = getAppUserProfileSample1();
        AppUserProfile appUserProfile2 = new AppUserProfile();
        assertThat(appUserProfile1).isNotEqualTo(appUserProfile2);

        appUserProfile2.setId(appUserProfile1.getId());
        assertThat(appUserProfile1).isEqualTo(appUserProfile2);

        appUserProfile2 = getAppUserProfileSample2();
        assertThat(appUserProfile1).isNotEqualTo(appUserProfile2);
    }
}
