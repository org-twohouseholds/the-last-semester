package com.extracreditstudios.thelastsemester.shared.messages;

public class Register {
    public record Request(String username, String password) {}
    public record Response(String session_token) {}
}
