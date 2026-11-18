package com.extracreditstudios.thelastsemester.server.shared.messages;

public class MessageHelper {

    public static boolean validatePassword(String password) {
        return password != null && password.length() >= 15
            && password.length() <= 128;
    }

    public static boolean validateUsername(String username) {
        return username != null && username.matches("[A-Za-z0-9_]{3,20}");
    }

    public static boolean validateSessionToken(String sessionToken) {
        return sessionToken != null && sessionToken.matches("[A-Za-z0-9+/]{42}[AEIMQUYcgkosw048]=");
    }
}
