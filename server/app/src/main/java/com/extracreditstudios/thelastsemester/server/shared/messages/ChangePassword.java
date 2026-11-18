
package com.extracreditstudios.thelastsemester.server.shared.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.javalin.http.Context;

public class ChangePassword {

    public record Request(
        @JsonProperty("old_password") String oldPassword,
        @JsonProperty("new_password") String newPassword
    ) {}

    public static Request validateRequest(Context ctx) {
        return ctx.bodyValidator(Request.class)
            .check(r -> r.oldPassword() != null
                && !r.oldPassword().isEmpty(),
                "Old password is required")
            .check(r -> r.newPassword() != null
                && r.newPassword().length() >= 15
                && r.newPassword().length() <= 128,
                "New password must be 15-128 characters long")
            .get();
    }
}
