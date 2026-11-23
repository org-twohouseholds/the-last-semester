package com.extracreditstudios.thelastsemester.shared.messages.game;

/** Data for a combat animation; filling these events is a separate task. */
public record TurnEventDTO(int sequence, EventType type, String actorId, String targetId,
                           String abilityName, int amount, int targetHpAfter, int targetEnergyAfter) {
}
