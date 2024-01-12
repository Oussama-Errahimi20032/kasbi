package com.projet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParcelleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Parcelle getParcelleSample1() {
        return new Parcelle().id(1L).libelle("libelle1").photo("photo1");
    }

    public static Parcelle getParcelleSample2() {
        return new Parcelle().id(2L).libelle("libelle2").photo("photo2");
    }

    public static Parcelle getParcelleRandomSampleGenerator() {
        return new Parcelle().id(longCount.incrementAndGet()).libelle(UUID.randomUUID().toString()).photo(UUID.randomUUID().toString());
    }
}
