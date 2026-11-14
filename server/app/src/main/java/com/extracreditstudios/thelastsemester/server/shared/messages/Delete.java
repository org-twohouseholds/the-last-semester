package com.extracreditstudios.thelastsemester.server.shared.messages;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

public class Delete {
    public record Request(String password) {}

    public static Request validateRequest(Context ctx) {
        return ctx.bodyValidator(Request.class)
            .check(request -> request.password() != null && !request.password().isEmpty(),
                "Password is required")
            .getOrThrow(errors -> new BadRequestResponse("Expected a JSON body with a password"));
    }
}
