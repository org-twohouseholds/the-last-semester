package com.extracreditstudios.thelastsemester;

import com.badlogic.gdx.Game;                                       // ✅ [1]
import com.badlogic.gdx.Gdx;                                        // ✅ [3]
import com.badlogic.gdx.Input;                                      // ✅ [3]
import com.badlogic.gdx.Preferences;                                // ✅ [2]
import com.badlogic.gdx.Screen;                                     // ✅ [4]
import com.badlogic.gdx.scenes.scene2d.ui.Skin;                     // ✅ [2]
import com.extracreditstudios.thelastsemester.screens.HomeScreen;   // ✅ [3]

public class TheLastSemesterGame extends Game {

    private Skin skin;
    private Preferences prefs;

    @Override
    public void create() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        prefs = Gdx.app.getPreferences("the-last-semester");
        setScreen(new HomeScreen(this));
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
