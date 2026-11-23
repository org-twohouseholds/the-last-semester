package com.extracreditstudios.thelastsemester.shared.messages;

public class SelectAction {
    public record Request(String tournament, String fight, Integer turn, Integer actionIndex) {}
}
