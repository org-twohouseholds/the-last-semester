package com.extracreditstudios.thelastsemester.shared.messages;

public class Delete {
    public record Request(String username, String password) {
        public Request(String password) {
            this(null, password);
        }
    }
}
