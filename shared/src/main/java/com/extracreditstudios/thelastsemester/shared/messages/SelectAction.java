package com.extracreditstudios.thelastsemester.shared.messages;

import java.util.UUID;

public class SelectAction {
    public record Request(String tournament, UUID fight, Integer turn, Integer actionIndex) {}
}
