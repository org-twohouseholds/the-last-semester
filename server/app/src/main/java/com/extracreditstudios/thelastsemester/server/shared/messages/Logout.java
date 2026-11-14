package com.extracreditstudios.thelastsemester.server.shared.messages;

import io.javalin.http.Context;

public class Logout {
    public record Request(String session_token) {}

    public static Logout.Request validateRequest(Context ctx) {
        return ctx.bodyValidator(Logout.Request.class)
            .check(r -> r.session_token() != null && r.session_token().matches("[A-Za-z0-9+/]{42}[AEIMQUYcgkosw048]="),"Invalid token")
            .get();
    }
}
