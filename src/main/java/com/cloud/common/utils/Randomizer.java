package com.cloud.common.utils;

import java.util.Random;

/**
 * Class used to generate randomized data
 */
public class Randomizer {
    private static final char[] dictionary = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    private static final Random random = new Random();

    public static String randomString(int length) {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = dictionary[random.nextInt(dictionary.length)];
        }

        return new String(chars);
    }
}
