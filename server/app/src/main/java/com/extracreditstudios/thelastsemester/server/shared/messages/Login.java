package com.extracreditstudios.thelastsemester.server.shared.messages;

import io.javalin.http.Context;

public class Login {
    public record Request(String username, String password) {}
    public record Response(String session_token) {}

    public static Request validateRequest(Context ctx) {
        return ctx.bodyValidator(Login.Request.class)
            .check(r -> r.username() != null && r.username().matches("[A-Za-z0-9_]{3,20}"),
                "Username must be 3-20 characters: letters, digits, _")
            .check(r -> r.password() != null && r.password().length() >= 15 &&
                r.password().length() <= 128, "Password must be 15-128 characters")
            .get();
    }
}
