
package com.extracreditstudios.thelastsemester.battleScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.extracreditstudios.thelastsemester.shared.messages.Protocol;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Clientseitige Kommunikation mit den Kampf-Endpunkten.
 * Die Match-ID wird später gesetzt, sobald sie verfügbar ist.
 */
public class FightClient {

    private final String baseUrl;
    private final String token;
    private String matchId;

    public FightClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public boolean hasMatchId() {
        return matchId != null && !matchId.isEmpty();
    }

    public void poll(Consumer<JsonValue> onOk, Consumer<String> onError) {
        if (!hasMatchId()) {
            onError.accept("Match-ID ist noch nicht verfügbar.");
            return;
        }

        Net.HttpRequest req = base(
            Net.HttpMethods.GET,
            "/api/matches/" + matchId + "/poll"
        );

        send(req, (code, body) -> {
            if (code == 200) {
                try {
                    onOk.accept(new JsonReader().parse(body));
                } catch (Exception e) {
                    onError.accept("Ungültige Serverantwort: " + e.getMessage());
                }
            } else {
                onError.accept("HTTP " + code + ": " + body);
            }
        }, onError);
    }

    public void select(String abilityName, int turnNumber,
                       Consumer<String> onResult) {
        if (!hasMatchId()) {
            onResult.accept("Match-ID ist noch nicht verfügbar.");
            return;
        }

        Net.HttpRequest req = base(
            Net.HttpMethods.POST,
            "/api/matches/" + matchId + "/selection"
        );

        req.setHeader("Content-Type", "application/json");

        String safeName = abilityName
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");

        req.setContent(
            "{\"abilityName\":\"" + safeName +
                "\",\"turnNumber\":" + turnNumber + "}"
        );

        send(req, (code, body) -> {
            onResult.accept(code == 200 ? null : "HTTP " + code + ": " + body);
        }, onResult);
    }

    private Net.HttpRequest base(String method, String path) {
        Net.HttpRequest req = new HttpRequestBuilder()
            .newRequest()
            .method(method)
            .url(baseUrl + path)
            .build();

        req.setHeader("Authorization", "Bearer " + token);
        req.setHeader("protocol-version", Protocol.VERSION);

        return req;
    }

    private void send(
        Net.HttpRequest req,
        BiConsumer<Integer, String> onResponse,
        Consumer<String> onError
    ) {
        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse res) {
                int code = res.getStatus().getStatusCode();
                String body = res.getResultAsString();

                Gdx.app.postRunnable(() -> onResponse.accept(code, body));
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() ->
                    onError.accept(String.valueOf(t))
                );
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() ->
                    onError.accept("Anfrage abgebrochen")
                );
            }
        });
    }
}
