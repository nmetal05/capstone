package com.allomed.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SymptomSearchRecommendationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SymptomSearchRecommendation getSymptomSearchRecommendationSample1() {
        return new SymptomSearchRecommendation().id(1L).rank(1).reasoning("reasoning1");
    }

    public static SymptomSearchRecommendation getSymptomSearchRecommendationSample2() {
        return new SymptomSearchRecommendation().id(2L).rank(2).reasoning("reasoning2");
    }

    public static SymptomSearchRecommendation getSymptomSearchRecommendationRandomSampleGenerator() {
        return new SymptomSearchRecommendation()
            .id(longCount.incrementAndGet())
            .rank(intCount.incrementAndGet())
            .reasoning(UUID.randomUUID().toString());
    }
}
