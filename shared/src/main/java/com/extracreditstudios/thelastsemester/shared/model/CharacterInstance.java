package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.ActiveEffect;
import com.extracreditstudios.thelastsemester.shared.model.effectsystem.EffectRegistry;

import java.util.ArrayList;
import java.util.List;

/** The running state of one fighter: current HP, current energy and equipped perks. */
public class CharacterInstance {

    private static final int MAX_PERKS = 3;

    private final Character character;
    private final List<Ability> perks = new ArrayList<>();
    private List<ActiveEffect> activeEffects;
    private int currentHP;
    private int currentEnergy;
    private int maxHP;
    private int maxEnergy;
    private double dmgAmplification;
    private double dmgReduction;

    public CharacterInstance(Character character) {
        this.character = character;
        this.currentHP = character.baseHealth();
        this.currentEnergy = character.baseEnergy();
        this.maxHP = character.baseHealth();
        this.maxEnergy = character.baseEnergy();
        this.dmgAmplification = 1.0;
        this.dmgReduction = 1.0;
        this.activeEffects = new ArrayList<>();
    }

    public void receiveDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage must not be negative: " + amount);
        }
        currentHP = Math.max(0, currentHP - amount);
    }

    public void useAbility(Ability ability, EffectRegistry effectRegistry) {
        if (!canUse(ability)) {
            throw new IllegalArgumentException("Ability cannot be used: " + ability);
        }
        currentEnergy -= ability.energyCost();

        for(String effectName : ability.effects()){
            ActiveEffect effect = new  ActiveEffect(effectName, effectRegistry.getEffect(effectName).getTurnDuration());
            activeEffects.add(effect);
        }
    }

    public boolean canUse(Ability ability) {
        return hasAbility(ability) && currentEnergy >= ability.energyCost();
    }

    public void addPerk(Ability perk) {
        if (perks.size() >= MAX_PERKS) {
            throw new IllegalStateException("All perk slots are taken");
        }
        perks.add(perk);
    }

    public boolean isDefeated() {
        return currentHP == 0;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    private boolean hasAbility(Ability ability) {
        return character.basicAttack().equals(ability) || perks.contains(ability);
    }

    public boolean flatHeal(int amount){
        // A defeated character stays defeated, no matter how big the heal is.
        if(this.currentHP <= 0){
            return false;
        }

        this.currentHP += amount;

        // Never heal above the maximum.
        if(this.currentHP > this.maxHP){
            this.currentHP = this.maxHP;
        }
        return true;
    }

    public boolean flatEnergyGain(int amount){
        if(amount + this.currentEnergy > this.maxEnergy){
            this.currentEnergy = this.maxEnergy;
        }else{
            this.currentEnergy += amount;
        }
        return true;
    }

    public List<ActiveEffect> getActiveEffects() {
        return activeEffects;
    }

    public void putActiveEffect(ActiveEffect activeEffect) {
        this.activeEffects.add(activeEffect);
    }

    public void removeActiveEffect(ActiveEffect activeEffect) {
        this.activeEffects.remove(activeEffect);
    }

    public void tickActiveEffects(){
       activeEffects.removeIf(e -> {
               if(e.getRemainingTurns() > 0){
                   e.setRemainingTurns(e.getRemainingTurns() - 1);
               }
               return e.getRemainingTurns() == 0;
           });
    }

    public double getDmgAmplification() {
        return dmgAmplification;
    }

    public void setDmgAmplification(double dmgAmplification) {
        this.dmgAmplification = dmgAmplification;
    }

    public double getDmgReduction() {
        return dmgReduction;
    }

    public void setDmgReduction(double dmgReduction) {
        this.dmgReduction = dmgReduction;
    }
}
