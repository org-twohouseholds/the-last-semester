package com.extracreditstudios.thelastsemester.server;

import com.extracreditstudios.thelastsemester.shared.messages.ChangePassword;
import com.extracreditstudios.thelastsemester.shared.messages.Delete;
import com.extracreditstudios.thelastsemester.shared.messages.Login;
import com.extracreditstudios.thelastsemester.shared.messages.Register;
import com.extracreditstudios.thelastsemester.shared.messages.SelectCharacter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

public class MessageValidator {

    public static boolean validateUsername(String username) {
        return username != null && username.matches("[A-Za-z0-9_]{3,20}");
    }

    public static boolean validatePassword(String password) {
        return password != null && password.length() >= 15
            && password.length() <= 128;
    }

    public static boolean validateSessionToken(String sessionToken) {
        return sessionToken != null && sessionToken.matches("[A-Za-z0-9+/]{42}[AEIMQUYcgkosw048]=");
    }

    public static ChangePassword.Request validateChangePasswordRequest(Context ctx) {
        return ctx.bodyValidator(ChangePassword.Request.class)
            .check(r -> r.old_password() != null
                    && !r.old_password().isEmpty(),
                "Old password is required")
            .check(r -> r.new_password() != null
                    && r.new_password().length() >= 15
                    && r.new_password().length() <= 128,
                "New password must be 15-128 characters long")
            .get();
    }

    private static final ObjectReader DELETE_REQUEST_READER = new ObjectMapper()
        .readerFor(JsonNode.class)
        .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    public static Delete.Request validateDeleteRequest(Context ctx) {
        try {
            JsonNode body = DELETE_REQUEST_READER.readValue(ctx.body());
            JsonNode password = body.path("password");
            if (body.isObject() && body.size() == 1 && password.isTextual()
                && !password.textValue().isEmpty()) {
                return new Delete.Request(password.textValue());
            }
        } catch (JsonProcessingException ignored) {}
        throw new BadRequestResponse("Expected a JSON body with a password");
    }

    public static SelectCharacter.Request validateSelectCharacterRequest(Context ctx) {
        try {
            JsonNode body = DELETE_REQUEST_READER.readValue(ctx.body());
            JsonNode characterId = body.path("character_id");
            // isInt() rejects strings, decimals and numbers outside the int range
            if (body.isObject() && body.size() == 1 && characterId.isInt()
                && SelectCharacter.isSelectableCharacterId(characterId.intValue())) {
                return new SelectCharacter.Request(characterId.intValue());
            }
        } catch (JsonProcessingException ignored) {}
        throw new BadRequestResponse("Expected a JSON body with a valid character_id");
    }

    public static Login.Request validateLoginRequest(Context ctx) {
        return ctx.bodyValidator(Login.Request.class)
            .check(r -> validateUsername(r.username()),
                "Username must be 3-20 characters: letters, digits, _")
            .check(r -> validatePassword(r.password()), "Password must be 15-128 characters")
            .get();
    }

    public static Register.Request validateRegisterRequest(Context ctx) {
        return ctx.bodyValidator(Register.Request.class)
            .check(r -> validateUsername(r.username()),
                "Username must be 3-20 characters: letters, digits, _")
            .check(r -> validatePassword(r.password()), "Password must be 15-128 characters")
            .get();
    }

}
