
package com.extracreditstudios.thelastsemester.server.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordsTest {

    @Test
    void correctPasswordIsAccepted() {
        String password = "MySecurePassword123";
        String hash = Passwords.hash(password);

        assertTrue(Passwords.verify(hash, password));
    }

    @Test
    void incorrectPasswordIsRejected() {
        String password = "MySecurePassword123";
        String hash = Passwords.hash(password);

        assertFalse(Passwords.verify(hash, "WrongPassword123"));
    }

    @Test
    void passwordIsNotStoredAsPlaintext() {
        String password = "MySecurePassword123";
        String hash = Passwords.hash(password);

        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$argon2id$"));
    }
}
