package com.author.crm.service;

import java.security.SecureRandom;
import java.util.function.Predicate;

public final class CredentialGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String DOT = ".";
    private static final String USERNAME_TEMPLATE = "%s%s%s";
    private static final String USERNAME_WITH_SUFFIX_TEMPLATE = "%s%d";
    private static final int FIRST_USERNAME_SUFFIX = 1;
    private static final int PASSWORD_LENGTH = 10;

    private CredentialGenerator() {
    }

    public static String username(String first, String last, Predicate<String> exists) {
        String safeFirst = (first == null) ? "" : first.trim();
        String safeLast = (last == null) ? "" : last.trim();
        String base = USERNAME_TEMPLATE.formatted(safeFirst, DOT, safeLast);
        String value = base;
        int suffix = FIRST_USERNAME_SUFFIX;

        while (exists.test(value)) {
            value = USERNAME_WITH_SUFFIX_TEMPLATE.formatted(base, suffix++);
        }

        return value;
    }

    public static String password() {
        StringBuilder result = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            result.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return result.toString();
    }
}
