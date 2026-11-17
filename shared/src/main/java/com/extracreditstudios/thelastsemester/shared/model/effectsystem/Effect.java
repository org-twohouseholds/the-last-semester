package com.extracreditstudios.thelastsemester.shared.model.effectsystem;

import com.extracreditstudios.thelastsemester.shared.model.CharacterInstance;

public class Effect {
    private String name;
    private String description;
    private EffectType type;
    private EffectTiming timing;
    private int turnDuration;
    private double value;

    public Effect(String name, String description, EffectType type, EffectTiming timing, int turnDuration, double value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.timing = timing;
        this.turnDuration = turnDuration;
        this.value = value;
    }

    public void apply(CharacterInstance source, CharacterInstance target){
        switch (type) {
            case HEAL:              source.flatHeal((int) this.value); break;
            case ENERGY_GAIN:       source.flatEnergyGain((int) this.value); break;

            //BOTH DMG AMPLIFICATION AS WELL AS DMG REDUCTION ARE MULTIPLIED --> PROBABLY NEEDS SOME SORT OF BALANCING
            case DMG_AMPLIFICATION: source.setDmgAmplification((source.getDmgAmplification() * this.value)); break;
            case DMG_REDUCTION: source.setDmgReduction((source.getDmgReduction() * this.value)); break;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public EffectType getType() {
        return type;
    }

    public EffectTiming getTiming() {
        return timing;
    }

    public int getTurnDuration() {
        return turnDuration;
    }

    public double getValue() {
        return value;
    }
}
