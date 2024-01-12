package com.projet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TypePlanteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TypePlante getTypePlanteSample1() {
        return new TypePlante().id(1L).name("name1").humiditeMax(1).humiditeMin(1).temperature(1);
    }

    public static TypePlante getTypePlanteSample2() {
        return new TypePlante().id(2L).name("name2").humiditeMax(2).humiditeMin(2).temperature(2);
    }

    public static TypePlante getTypePlanteRandomSampleGenerator() {
        return new TypePlante()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .humiditeMax(intCount.incrementAndGet())
            .humiditeMin(intCount.incrementAndGet())
            .temperature(intCount.incrementAndGet());
    }
}
