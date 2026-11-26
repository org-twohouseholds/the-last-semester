package com.extracreditstudios.thelastsemester.shared.messages;

public class SelectCharacter {
    // TODO: Open team decision: there is no binding character ID mapping yet.
    //  The combat rules define 4 characters, so IDs 0-3 are accepted for now.
    public static final int MIN_CHARACTER_ID = 0;
    public static final int MAX_CHARACTER_ID = 3;

    public record Request(int character_id) {}

    public static boolean isSelectableCharacterId(int characterId) {
        return characterId >= MIN_CHARACTER_ID && characterId <= MAX_CHARACTER_ID;
    }
}
