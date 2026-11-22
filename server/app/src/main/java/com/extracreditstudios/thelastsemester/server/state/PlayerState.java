package com.extracreditstudios.thelastsemester.server.state;

import com.extracreditstudios.thelastsemester.shared.model.Ability;
import com.extracreditstudios.thelastsemester.shared.model.TournamentParticipation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** One player in RAM. Character, HP, energy and perks live in participation. */
public class PlayerState {
    public final UUID playerId = UUID.randomUUID();
    public final Long accountId;
    public final String username;
    public boolean bot;
    public long lastSeenMillis = System.currentTimeMillis();
    public Integer characterId;
    public TournamentParticipation participation;
    public int cp;
    public final List<Ability> shopOffer = new ArrayList<>();

    /** A null accountId identifies a bot without an account. */
    public PlayerState(Long accountId, String username) {
        this.accountId = accountId;
        this.username = username;
        this.bot = accountId == null;
    }
}
