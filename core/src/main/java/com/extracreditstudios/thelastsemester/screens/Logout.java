package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

public final class Logout {

    /** TODO: LOGOUT_URL nachfragen und ändern */
    private static final String LOGOUT_URL = "https://the-last-semester.twohouseholds.com/auth/logout";

    /** TODO: Prüft der Server beim Logout auch die Protokoll-Version? Wenn ja: welche? (ANNAHME: "1.0.0") */
    private static final String PROTOCOL_VERSION = "1.0.0";

    /** TODO: WIE wird der Token mitgeschickt? Im Header oder als JSON im Body?
    *      Wenn Header: wie heißt er? (ANNAHME: Header "Authorization" mit "Bearer <token>") -> Header
           (Wenn Body: wie heißt das Feld im JSON?)*/
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    // ====================================================================================

    private Logout() {

    }

    public static void run(Runnable whenDone) {
        String token = Session.getToken();

        if (token == null) {
            finish(whenDone);
            return;
        }

        // Anfrage zusammenbauen
        Net.HttpRequest request = new HttpRequestBuilder().newRequest()
            .method(Net.HttpMethods.POST)
            .url(LOGOUT_URL)
            .header("protocol-version", PROTOCOL_VERSION)
            .header(TOKEN_HEADER, TOKEN_PREFIX + token)
            .timeout(10000) // nach 10 Sekunden aufgeben
            .build();

        // Abschicken
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                // Nur den Code loggen, nie den Token
                Gdx.app.log("Logout", "Antwort-Code: " + response.getStatus().getStatusCode());
                finish(whenDone);
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("Logout", "Server nicht erreichbar, melde trotzdem lokal ab", t);
                finish(whenDone);
            }

            @Override
            public void cancelled() {
                finish(whenDone);
            }
        });
    }

    /** Token im Spiel vergessen und zurück in den UI-Thread, dort "whenDone" ausführen. */
    private static void finish(Runnable whenDone) {
        Gdx.app.postRunnable(() -> {
            Session.end();
            if (whenDone != null) {
                whenDone.run();
            }
        });
    }
}
