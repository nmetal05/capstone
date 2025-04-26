package com.allomed.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DoctorDocumentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DoctorDocument getDoctorDocumentSample1() {
        return new DoctorDocument().id(1L).fileName("fileName1");
    }

    public static DoctorDocument getDoctorDocumentSample2() {
        return new DoctorDocument().id(2L).fileName("fileName2");
    }

    public static DoctorDocument getDoctorDocumentRandomSampleGenerator() {
        return new DoctorDocument().id(longCount.incrementAndGet()).fileName(UUID.randomUUID().toString());
    }
}
