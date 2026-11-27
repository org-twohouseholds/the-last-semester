package com.extracreditstudios.thelastsemester.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.extracreditstudios.thelastsemester.screens.Session;

public final class LobbyApi {
    private static final String BASE_URL = "https://the-last-semester.twohouseholds.com/api";
    private static final String JOIN_URL = BASE_URL + "/lobby/join";
    private static final String QUIT_URL = BASE_URL + "/lobby/quit";

    private static final String PROTOCOL_VERSION = "1.0.0";

    private static final int STATUS_OK = 204;
    private static final int STATUS_UNAUTHORIZED = 401;
    private static final int STATUS_CONFLICT = 409;
    private static final int STATUS_OUTDATED = 426;

    /** Für Lobby-Screen... */
    public interface JoinListener {
        void joined();                 // geklappt -> Polling läuft, Spielerliste kommt von dort
        void failed(String message);   // nicht geklappt -> Meldung anzeigen
    }

    private LobbyApi() {

    }

    /** Lobby beitreten. Ergebnis kommt über den listener in Haupt Thread */
    public static void join(JoinListener listener) {
        String token = Session.getToken();
        if (token == null) {
            joinFailed(listener, "Bitte zuerst einloggen");
            return;
        }

        Gdx.net.sendHttpRequest(buildRequest(JOIN_URL, token), new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                int code = response.getStatus().getStatusCode();
                Gdx.app.log("LobbyApi", "Join Antwort-Code: " + code); // nur Code loggen

                if (code == STATUS_OK) {
                    joinSucceeded(listener);
                } else if (code == STATUS_CONFLICT) {
                    joinFailed(listener, "Du bist schon in einer Lobby");
                } else if (code == STATUS_UNAUTHORIZED) {
                    joinFailed(listener, "Bitte neu einloggen");
                } else if (code == STATUS_OUTDATED) {
                    joinFailed(listener, "Bitte Spiel aktualisieren (Protokoll-Version)");
                } else {
                    joinFailed(listener, "Serverfehler (" + code + ")");
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("LobbyApi", "Join: keine Verbindung", t);
                joinFailed(listener, "Keine Verbindung zum Server");
            }

            @Override
            public void cancelled() {
                joinFailed(listener, "Anfrage abgebrochen");
            }
        });
    }

    /** Join geklappt... */
    private static void joinSucceeded(JoinListener listener) {
        Gdx.app.postRunnable(() -> { // zurück in den Haupt-Thread
            // TODO: Entkommentieren hier
            // LobbyPoller.start();
            listener.joined();
        });
    }

    /** Join nicht geklappt  */
    private static void joinFailed(JoinListener listener, String message) {
        Gdx.app.postRunnable(() -> listener.failed(message)); // zurück in Haupt-Thread
    }

    public static void quit(Runnable whenDone) {
        String token = Session.getToken();


        if (token == null) {
            quitFinished(whenDone);
            return;
        }

        Gdx.net.sendHttpRequest(buildRequest(QUIT_URL, token), new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse response) {
                Gdx.app.log("LobbyApi", "Quit Antwort-Code: " + response.getStatus().getStatusCode());
                quitFinished(whenDone);
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("LobbyApi", "Quit: keine Verbindung, verlasse trotzdem lokal", t);
                quitFinished(whenDone);
            }

            @Override
            public void cancelled() {
                quitFinished(whenDone);
            }
        });
    }

    /** Polling stoppen */
    private static void quitFinished(Runnable whenDone) {
        Gdx.app.postRunnable(() -> {
            // TODO: Entkommentieren hier
            // LobbyPoller.stop();
            if (whenDone != null) {
                whenDone.run();
            }
        });
    }


    // Hilfsmethode
    /** POST-Anfrage OHNE Inhalt, nur mit Token im Header */
    private static Net.HttpRequest buildRequest(String url, String token) {
        return new HttpRequestBuilder().newRequest()
            .method(Net.HttpMethods.POST)
            .url(url)
            .header("protocol-version", PROTOCOL_VERSION)
            .header("Authorization", "Bearer " + token)
            .timeout(10000)
            .build();
    }
}
