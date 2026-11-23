package com.extracreditstudios.thelastsemester.screens;


 /** TODO: Soll man eingeloggt bleiben, wenn die App geschlossen wird? */
public final class Session {

    private static String token;

    private Session() {

    }

    public static void start(String newToken) {
        token = newToken;
    }

    public static String getToken() {
        return token;
    }

    public static boolean isLoggedIn() {
        return token != null;
    }

    /** Beim Logout wird Token auf null gesetzt */
    public static void end() {
        token = null;
    }
}
