package com.allomed.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SpecializationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Specialization getSpecializationSample1() {
        return new Specialization().id(1L).name("name1").description("description1");
    }

    public static Specialization getSpecializationSample2() {
        return new Specialization().id(2L).name("name2").description("description2");
    }

    public static Specialization getSpecializationRandomSampleGenerator() {
        return new Specialization()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
