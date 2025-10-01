package com.hanyahunya.auth.domain.util;

import java.security.SecureRandom;

public class RandomString {
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String DIGITS = "0123456789";

    public static String generateAlphanumeric(int length) {
        return generate(length, ALPHANUMERIC);
    }

    public static String generateNumeric(int length) {
        return generate(length, DIGITS);
    }

    private static String generate(int length, String characterSet) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characterSet.length());
            sb.append(characterSet.charAt(randomIndex));
        }
        return sb.toString();
    }
}
