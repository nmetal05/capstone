package com.allomed.app.service.mapper;

import static com.allomed.app.domain.AppUserProfileAsserts.*;
import static com.allomed.app.domain.AppUserProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppUserProfileMapperTest {

    private AppUserProfileMapper appUserProfileMapper;

    @BeforeEach
    void setUp() {
        appUserProfileMapper = new AppUserProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppUserProfileSample1();
        var actual = appUserProfileMapper.toEntity(appUserProfileMapper.toDto(expected));
        assertAppUserProfileAllPropertiesEquals(expected, actual);
    }
}
