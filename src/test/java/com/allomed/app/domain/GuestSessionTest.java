package com.allomed.app.domain;

import static com.allomed.app.domain.GuestSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.allomed.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GuestSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GuestSession.class);
        GuestSession guestSession1 = getGuestSessionSample1();
        GuestSession guestSession2 = new GuestSession();
        assertThat(guestSession1).isNotEqualTo(guestSession2);

        guestSession2.setId(guestSession1.getId());
        assertThat(guestSession1).isEqualTo(guestSession2);

        guestSession2 = getGuestSessionSample2();
        assertThat(guestSession1).isNotEqualTo(guestSession2);
    }
}
