package com.allomed.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GuestSessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GuestSessionDTO.class);
        GuestSessionDTO guestSessionDTO1 = new GuestSessionDTO();
        guestSessionDTO1.setId(1L);
        GuestSessionDTO guestSessionDTO2 = new GuestSessionDTO();
        assertThat(guestSessionDTO1).isNotEqualTo(guestSessionDTO2);
        guestSessionDTO2.setId(guestSessionDTO1.getId());
        assertThat(guestSessionDTO1).isEqualTo(guestSessionDTO2);
        guestSessionDTO2.setId(2L);
        assertThat(guestSessionDTO1).isNotEqualTo(guestSessionDTO2);
        guestSessionDTO1.setId(null);
        assertThat(guestSessionDTO1).isNotEqualTo(guestSessionDTO2);
    }
}
