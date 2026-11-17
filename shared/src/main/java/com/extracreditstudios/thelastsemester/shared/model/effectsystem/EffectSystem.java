package com.extracreditstudios.thelastsemester.shared.model.effectsystem;
import com.extracreditstudios.thelastsemester.shared.model.CharacterInstance;

public class EffectSystem {

    //Effects have to be loaded in like the abilities via the JSON
    private final EffectRegistry registry;

    public EffectSystem(){
        this.registry = new EffectRegistry();
    }

    public void applyEffects(CharacterInstance source, CharacterInstance target, EffectTiming timing) {
        if(source.getActiveEffects().equals(null)){
            return;
        }

        for(ActiveEffect e: source.getActiveEffects()){
            if(registry.getEffect(e.getEffectName()).getTiming() == timing){
                registry.getEffect(e.getEffectName()).apply(source, target);
            }
        }

        if(timing == EffectTiming.AFTER_TURN){
            source.tickActiveEffects();
        }
    }

    public EffectRegistry getRegistry() {
        return registry;
    }


}
