package com.extracreditstudios.thelastsemester.shared.model;

public class Effect {
    private String name;
    private String description;
    private EffectType type;
    private EffectTiming timing;
    private boolean activateOnce;
    private double value;

    public double apply(CharacterInstance source, CharacterInstance target, double incomingValue){
        //TODO
        return 0;
    }
}
