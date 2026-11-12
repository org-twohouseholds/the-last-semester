package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;

public class PlaceholderScreen extends BaseScreen {

    private final String title;

    public PlaceholderScreen(TheLastSemesterGame game, String title) {
        super(game);
        this.title = title;
    }

    @Override
    protected void buildUi() {
        Label titleLabel = new Label(title, skin);
        titleLabel.setFontScale(1.6f);

        root.add(titleLabel).padBottom(20).row();
        root.add(new Label("Coming soon", skin)).padBottom(40).row();
        root.add(button("Back", this::onBack)).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).row();
    }

    @Override
    protected void onBack() {
        game.goTo(new HomeScreen(game));
    }
}
