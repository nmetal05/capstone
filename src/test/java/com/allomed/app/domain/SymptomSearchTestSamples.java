package com.allomed.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SymptomSearchTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SymptomSearch getSymptomSearchSample1() {
        return new SymptomSearch().id(1L).symptoms("symptoms1");
    }

    public static SymptomSearch getSymptomSearchSample2() {
        return new SymptomSearch().id(2L).symptoms("symptoms2");
    }

    public static SymptomSearch getSymptomSearchRandomSampleGenerator() {
        return new SymptomSearch().id(longCount.incrementAndGet()).symptoms(UUID.randomUUID().toString());
    }
}
