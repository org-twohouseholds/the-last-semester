package com.extracreditstudios.thelastsemester.shared.model.effectsystem;

public class ActiveEffect {
    private String effectName;

    public ActiveEffect(String effectName, int remainingTurns) {
        this.effectName = effectName;
        this.remainingTurns = remainingTurns;
    }

    private int remainingTurns; // -1 for Passives/whole game

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public String getEffectName() {
        return effectName;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }
}
