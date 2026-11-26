package com.extracreditstudios.thelastsemester.shared.messages;

public class Login {
    public record Request(String username, String password) {}
    public record Response(String session_token) {}
}
