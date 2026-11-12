package com.author.crm.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CredentialGeneratorTest {
    @Test
    void joinsTrimmedNamesWithADot() {
        assertEquals("John.Smith", CredentialGenerator.username(" John ", " Smith ", value -> false));
    }

    @Test
    void incrementsSuffixUntilUsernameIsAvailable() {
        Set<String> existing = Set.of("John.Smith", "John.Smith1", "John.Smith2");

        assertEquals("John.Smith3", CredentialGenerator.username("John", "Smith", existing::contains));
    }

    @Test
    void generatesTenCharacterAlphanumericPasswords() {
        for (int i = 0; i < 20; i++) {
            assertTrue(CredentialGenerator.password().matches("[A-Za-z0-9]{10}"));
        }
    }
}
