package com.allomed.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GuestSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static GuestSession getGuestSessionSample1() {
        return new GuestSession().id(1L).sessionId("sessionId1").ipAddress("ipAddress1").userAgent("userAgent1");
    }

    public static GuestSession getGuestSessionSample2() {
        return new GuestSession().id(2L).sessionId("sessionId2").ipAddress("ipAddress2").userAgent("userAgent2");
    }

    public static GuestSession getGuestSessionRandomSampleGenerator() {
        return new GuestSession()
            .id(longCount.incrementAndGet())
            .sessionId(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .userAgent(UUID.randomUUID().toString());
    }
}
