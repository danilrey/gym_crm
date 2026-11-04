package com.author.crm.service;

import java.security.SecureRandom;
import java.util.function.Predicate;

public final class CredentialGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DOT = ".";

    private CredentialGenerator() {
    }

    public static String username(String first, String last, Predicate<String> exists) {
        String safeFirst = (first == null) ? "" : first.trim();
        String safeLast = (last == null) ? "" : last.trim();
        String base = safeFirst + DOT + safeLast;
        String value = base;
        int suffix = 1;

        while (exists.test(value)) {
            value = base + suffix++;
        }

        return value;
    }

    public static String password() {
        StringBuilder result = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            result.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return result.toString();
    }
}
