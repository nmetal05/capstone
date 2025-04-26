package com.allomed.app.domain;

import java.util.UUID;

public class AppUserProfileTestSamples {

    public static AppUserProfile getAppUserProfileSample1() {
        return new AppUserProfile().id("id1").lastLoginIp("lastLoginIp1").lastUserAgent("lastUserAgent1");
    }

    public static AppUserProfile getAppUserProfileSample2() {
        return new AppUserProfile().id("id2").lastLoginIp("lastLoginIp2").lastUserAgent("lastUserAgent2");
    }

    public static AppUserProfile getAppUserProfileRandomSampleGenerator() {
        return new AppUserProfile()
            .id(UUID.randomUUID().toString())
            .lastLoginIp(UUID.randomUUID().toString())
            .lastUserAgent(UUID.randomUUID().toString());
    }
}
