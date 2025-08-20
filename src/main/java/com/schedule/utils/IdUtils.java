package com.schedule.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class IdUtils {

    public static String generateId() {
        return RandomStringUtils.randomAlphabetic(20);
    }
}
