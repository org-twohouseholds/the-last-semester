package com.extracreditstudios.thelastsemester.shared.messages;

public class Delete {
    public record Request(String password) {}

    public record WithCredentialsRequest(String username, String password) {}
}
