package com.allomed.app.service.mapper;

import static com.allomed.app.domain.GuestSessionAsserts.*;
import static com.allomed.app.domain.GuestSessionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuestSessionMapperTest {

    private GuestSessionMapper guestSessionMapper;

    @BeforeEach
    void setUp() {
        guestSessionMapper = new GuestSessionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGuestSessionSample1();
        var actual = guestSessionMapper.toEntity(guestSessionMapper.toDto(expected));
        assertGuestSessionAllPropertiesEquals(expected, actual);
    }
}
