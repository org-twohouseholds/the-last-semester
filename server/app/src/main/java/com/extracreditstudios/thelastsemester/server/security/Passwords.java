package com.extracreditstudios.thelastsemester.server.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Passwords {
    private static final Argon2 ARGON2ID = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    public static String hash (String password) {
        // parameters are OWASP's minimum recommendation for Argon2id
        return ARGON2ID.hash(2, 19456, 1, password.toCharArray());
    }

    public static boolean verify (String hash, String password) {
        return ARGON2ID.verify(hash, password.toCharArray());
    }
}
