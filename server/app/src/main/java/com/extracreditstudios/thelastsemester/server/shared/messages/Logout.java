package com.extracreditstudios.thelastsemester.server.shared.messages;

import io.javalin.http.Context;

public class Logout {
    public record Request(String session_token) {}

    public static Logout.Request validateRequest(Context ctx) {
        return ctx.bodyValidator(Logout.Request.class)
            .check(r -> MessageHelper.validateSessionToken(r.session_token()),"Invalid token")
            .get();
    }
}
