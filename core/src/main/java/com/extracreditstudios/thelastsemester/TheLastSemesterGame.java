package com.extracreditstudios.thelastsemester;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.extracreditstudios.thelastsemester.screens.HomeScreen;
import java.util.ArrayList;
import java.util.List;
import com.extracreditstudios.thelastsemester.battleScreen.Attack;

public class TheLastSemesterGame extends Game {

    private Skin skin;
    private Preferences prefs;
    private int cp = 0;
    private final List<Attack> unlockedAttacks = new ArrayList<>();

    public int getCP() {
        return cp;
    }

    public void addCP(int amount) {
        cp += amount;
    }

    @Override
    public void create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        prefs = Gdx.app.getPreferences("the-last-semester");
        //setScreen(new HomeScreen(this));
        setScreen(new HomeScreen(this));
        //setScreen(new LoginScreen(this));
        //gives you the basic attack
        unlockedAttacks.add(new Attack("Basic Attack", 10, 0));
    }

    public void goTo(Screen next) {
        Gdx.app.postRunnable(() -> {
            Screen old = getScreen();
            setScreen(next);
            if (old != null && old != next) {
                old.dispose();
            }
        });
    }
    public void addAttack(Attack attack) {
        unlockedAttacks.add(attack);
    }

    public List<Attack> getUnlockedAttacks() {
        return unlockedAttacks;
    }
    public void resetAttacks() {
        unlockedAttacks.clear();
        unlockedAttacks.add(new Attack("Basic Attack", 20, 0));
    }
    //check if you have a certain attack
    public boolean doYouHaveThisAttack(Attack a){
        return unlockedAttacks.contains(a);
    }

    public Skin getSkin() {
        return skin;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    @Override
    public void dispose() {
        Screen current = getScreen();
        super.dispose();
        if (current != null) {
            current.dispose();
        }
        skin.dispose();
    }
}
