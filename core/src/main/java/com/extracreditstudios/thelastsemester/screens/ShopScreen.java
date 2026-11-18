package com.extracreditstudios.thelastsemester.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.extracreditstudios.thelastsemester.TheLastSemesterGame;
import com.extracreditstudios.thelastsemester.shared.model.Ability;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Shop-Fenster vor jedem Kampf (Gameflow Phase 1 und 4).
 * Zeigt 2 zufällige Perks, der Spieler kauft einen davon innerhalb von 15 Sekunden.
 * Runde 1 ist gratis, danach kosten die Perks CP.
 */
public class ShopScreen extends BaseScreen {

    private static final float CHOICE_TIME = 15f;
    private static final int OFFER_SIZE = 2;
    private static final int MAX_ROUNDS = 3;

    // Platzhalter: CP pro gewonnenem Kampf, bis der BattleScreen echte Belohnungen liefert
    private static final int CP_PER_WIN = 50;

    /** Ein Perk im Shop mit seinem normalen Preis. */
    private record Perk(Ability ability, int price) {
    }

    // Platzhalter-Werte (Name, Damage, Energy Cost, Speed), bis das Team das Balancing festlegt
    private static final List<Perk> ALL_PERKS = List.of(
        new Perk(new Ability("Coffee Rush", 15, 10, 9), 20),
        new Perk(new Ability("All-Nighter", 35, 30, 4), 40),
        new Perk(new Ability("Copy & Paste", 20, 15, 7), 25),
        new Perk(new Ability("Group Project", 25, 20, 5), 30),
        new Perk(new Ability("Deadline Panic", 40, 40, 8), 50),
        new Perk(new Ability("Office Hours", 10, 5, 6), 15),
        new Perk(new Ability("Mensa Special", 30, 25, 3), 35),
        new Perk(new Ability("Plagiarism", 45, 50, 2), 60)
    );

    // Zustand über die Runden eines Turniers (später vom Server)
    private static final List<Ability> ownedPerks = new ArrayList<>();
    private static int cp = 0;

    private final int round;
    private final Screen nextScreen;
    private final Skin lobbySkin;
    private final UIFactory ui;
    private final List<Perk> offers = new ArrayList<>();
    private final Random random = new Random();

    private float timeLeft = CHOICE_TIME;
    private boolean done = false;
    private Label timerLabel;

    /**
     * @param round      Shop-Runde 1 bis 3 (1 = Turnierbeginn, setzt Perks und CP zurück)
     * @param nextScreen Screen nach dem Kauf, normalerweise der BattleScreen
     */
    public ShopScreen(TheLastSemesterGame game, int round, Screen nextScreen) {
        super(game);
        this.round = round;
        this.nextScreen = nextScreen;

        lobbySkin = new Skin(Gdx.files.internal("ui/lobby/design_sammlung.json"));
        ui = new UIFactory(lobbySkin);

        if (round == 1) {
            ownedPerks.clear();
            cp = 0;
        } else {
            cp += CP_PER_WIN;
        }

        // 2 zufällige Perks, die der Spieler noch nicht hat
        List<Perk> candidates = new ArrayList<>();
        for (Perk perk : ALL_PERKS) {
            if (!ownedPerks.contains(perk.ability())) {
                candidates.add(perk);
            }
        }
        Collections.shuffle(candidates, random);
        for (int i = 0; i < Math.min(OFFER_SIZE, candidates.size()); i++) {
            offers.add(candidates.get(i));
        }
    }

    /** Alle bisher gekauften Perks, z. B. für die Angriffs-Buttons im BattleScreen. */
    public static List<Ability> getOwnedPerks() {
        return Collections.unmodifiableList(ownedPerks);
    }

    @Override
    protected void buildUi() {
        root.pad(40, 20, 30, 20);

        Label title = ui.createLabel("SHOP", "title");
        Label roundLabel = ui.createLabel("Runde " + round + "/" + MAX_ROUNDS, "small");
        Label cpLabel = ui.createLabel("CP: " + cp, "default");
        cpLabel.setColor(lobbySkin.getColor("text_gold"));

        String hint = round == 1 ? "Wähle einen Perk (gratis)" : "Wähle einen Perk";
        Label hintLabel = ui.createLabel(hint, "small");

        timerLabel = ui.createLabel("", "title");
        updateTimerLabel();

        root.add(title).padBottom(10).row();
        root.add(roundLabel).padBottom(18).row();
        root.add(cpLabel).padBottom(20).row();
        root.add(timerLabel).padBottom(20).row();
        root.add(hintLabel).padBottom(15).row();

        for (Perk perk : offers) {
            root.add(createOfferButton(perk))
                .width(UIFactory.FULL_BUTTON_WIDTH)
                .height(110)
                .padBottom(15)
                .row();
        }

        if (!ownedPerks.isEmpty()) {
            StringBuilder names = new StringBuilder("Deine Perks:");
            for (Ability perk : ownedPerks) {
                names.append("\n").append(perk.name());
            }
            Label ownedLabel = ui.createLabel(names.toString(), "small");
            ownedLabel.setAlignment(Align.center);
            root.add(ownedLabel).padTop(10).row();
        }
    }

    private Button createOfferButton(Perk perk) {
        Ability a = perk.ability();
        int price = priceOf(perk);
        String priceText = price == 0 ? "GRATIS" : price + " CP";

        Label name = ui.createLabel(a.name(), "default");
        Label stats = ui.createLabel(
            "DMG " + a.baseDamage() + "  EN " + a.energyCost() + "  SPD " + a.speed(), "small");
        Label priceLabel = ui.createLabel(priceText, "small");
        priceLabel.setColor(lobbySkin.getColor("text_gold"));
        name.setFontScale(0.8f);
        stats.setFontScale(0.75f);
        priceLabel.setFontScale(0.75f);

        Button button = new Button(lobbySkin);
        button.add(name).padBottom(10).row();
        button.add(stats).padBottom(10).row();
        button.add(priceLabel);

        if (cp < price) {
            // grau: CP reichen nicht (gleiche Farbe wie bei den Attack-Buttons)
            button.setDisabled(true);
            button.setColor(0.5f, 0.5f, 0.5f, 1f);
        }

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!button.isDisabled()) {
                    buy(perk);
                }
            }
        });
        return button;
    }

    private int priceOf(Perk perk) {
        return round == 1 ? 0 : perk.price();
    }

    private void buy(Perk perk) {
        if (done) {
            return;
        }
        done = true;
        cp -= priceOf(perk);
        ownedPerks.add(perk.ability());
        game.goTo(nextScreen);
    }

    /** Zeit abgelaufen: zufälligen bezahlbaren Perk kaufen, sonst ohne Kauf weiter. */
    private void timeUp() {
        List<Perk> affordable = new ArrayList<>();
        for (Perk perk : offers) {
            if (cp >= priceOf(perk)) {
                affordable.add(perk);
            }
        }
        if (affordable.isEmpty()) {
            done = true;
            game.goTo(nextScreen);
        } else {
            buy(affordable.get(random.nextInt(affordable.size())));
        }
    }

    private void updateTimerLabel() {
        int seconds = Math.max(0, (int) Math.ceil(timeLeft));
        timerLabel.setText(String.valueOf(seconds));
        timerLabel.setColor(seconds <= 3 ? Color.RED : Color.WHITE);
    }

    @Override
    public void render(float delta) {
        if (!done) {
            timeLeft -= Math.min(delta, 0.05f);
            updateTimerLabel();
            if (timeLeft <= 0) {
                timeUp();
            }
        }
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        lobbySkin.dispose();
    }
}
