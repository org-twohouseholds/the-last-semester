package com.extracreditstudios.thelastsemester.shared.model.effectsystem;

import java.util.HashMap;
import java.util.Map;

//This class is created so that every effect only needs one instance and can be used my every character
public class EffectRegistry {
    private final Map<String, Effect> effects = new HashMap<>();

    public void register(Effect e) { effects.put(e.getName(), e); }

    public Effect getEffect(String name) {
        Effect e = effects.get(name);
        return e;
    }
}
