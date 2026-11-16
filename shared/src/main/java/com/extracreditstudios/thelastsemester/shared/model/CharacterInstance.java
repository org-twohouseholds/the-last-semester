package com.extracreditstudios.thelastsemester.shared.model;

import com.extracreditstudios.thelastsemester.shared.model.effectsystem.ActiveEffect;

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
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage must not be negative: " + amount);
        }
        currentHP = Math.max(0, currentHP - amount);
    }

    public void useAbility(Ability ability) {
        if (!canUse(ability)) {
            throw new IllegalArgumentException("Ability cannot be used: " + ability);
        }
        currentEnergy -= ability.energyCost();
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
        if(amount + this.currentHP > this.maxHP){
            this.currentHP = this.maxHP;
        }else if( this.currentHP <= 0){
            return true;
        }else{
            this.currentHP += amount;
        }
        return true;
    }

    public boolean flatEnergyGain(int amount){
        if(amount + this.currentEnergy > this.maxEnergy){
            this.currentEnergy = this.maxEnergy;
        }else{
            this.currentHP += amount;
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
