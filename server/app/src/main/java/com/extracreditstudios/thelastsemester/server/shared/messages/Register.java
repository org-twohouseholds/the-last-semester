package com.extracreditstudios.thelastsemester.server.shared.messages;

import io.javalin.http.Context;

// TODO: move message definitions to shared/
public class Register {
    public record Request(String username, String password) {}
    public record Response(String session_token) {}

    public static Request validateRequest(Context ctx) {
        return ctx.bodyValidator(Register.Request.class)
            .check(r -> MessageHelper.validateUsername(r.username()),
                "Username must be 3-20 characters: letters, digits, _")
            .check(r -> MessageHelper.validatePassword(r.password()), "Password must be 15-128 characters")
            .get();
    }
}
