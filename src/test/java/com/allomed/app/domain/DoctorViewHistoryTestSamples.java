package com.allomed.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DoctorViewHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DoctorViewHistory getDoctorViewHistorySample1() {
        return new DoctorViewHistory().id(1L);
    }

    public static DoctorViewHistory getDoctorViewHistorySample2() {
        return new DoctorViewHistory().id(2L);
    }

    public static DoctorViewHistory getDoctorViewHistoryRandomSampleGenerator() {
        return new DoctorViewHistory().id(longCount.incrementAndGet());
    }
}
