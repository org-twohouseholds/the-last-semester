package com.extracreditstudios.thelastsemester.server.shared.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

public class Delete {
    public record Request(String password) {}

    private static final ObjectReader REQUEST_READER = new ObjectMapper()
        .readerFor(JsonNode.class)
        .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    public static Request validateRequest(Context ctx) {
        try {
            JsonNode body = REQUEST_READER.readValue(ctx.body());
            JsonNode password = body.path("password");
            if (body.isObject() && body.size() == 1 && password.isTextual()
                && !password.textValue().isEmpty()) {
                return new Request(password.textValue());
            }
        } catch (JsonProcessingException ignored) {}
        throw new BadRequestResponse("Expected a JSON body with a password");
    }
}
