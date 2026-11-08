package com.extracreditstudios.thelastsemester.server.shared.messages;

// TODO: move message definitions to shared/
public class Register {
    public record Request(String username, String password) {}
    public record Response(String session_token) {}
}
