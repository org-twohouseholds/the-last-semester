package com.extracreditstudios.thelastsemester.server.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public class SessionTokens {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String newToken() {
        byte[] binary_token = new byte[32];
        SECURE_RANDOM.nextBytes(binary_token);
        return Base64.getEncoder().encodeToString(binary_token);
    }

    public static String hash(String token) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e); // every JRE includes SHA-256
        }
    }
}
