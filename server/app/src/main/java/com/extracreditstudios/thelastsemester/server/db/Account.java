package com.extracreditstudios.thelastsemester.server.db;

public record Account(String username, String password_hash, String session_token, int selected_character_id) {
}
