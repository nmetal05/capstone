package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppUserProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUserProfileDTO.class);
        AppUserProfileDTO appUserProfileDTO1 = new AppUserProfileDTO();
        appUserProfileDTO1.setId("id1");
        AppUserProfileDTO appUserProfileDTO2 = new AppUserProfileDTO();
        assertThat(appUserProfileDTO1).isNotEqualTo(appUserProfileDTO2);
        appUserProfileDTO2.setId(appUserProfileDTO1.getId());
        assertThat(appUserProfileDTO1).isEqualTo(appUserProfileDTO2);
        appUserProfileDTO2.setId("id2");
        assertThat(appUserProfileDTO1).isNotEqualTo(appUserProfileDTO2);
        appUserProfileDTO1.setId(null);
        assertThat(appUserProfileDTO1).isNotEqualTo(appUserProfileDTO2);
    }
}
