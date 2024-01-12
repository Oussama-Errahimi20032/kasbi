package com.projet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppUser getAppUserSample1() {
        return new AppUser()
            .id(1L)
            .username("username1")
            .email("email1")
            .password("password1")
            .address("address1")
            .phone("phone1")
            .role("role1")
            .image("image1");
    }

    public static AppUser getAppUserSample2() {
        return new AppUser()
            .id(2L)
            .username("username2")
            .email("email2")
            .password("password2")
            .address("address2")
            .phone("phone2")
            .role("role2")
            .image("image2");
    }

    public static AppUser getAppUserRandomSampleGenerator() {
        return new AppUser()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .role(UUID.randomUUID().toString())
            .image(UUID.randomUUID().toString());
    }
}
