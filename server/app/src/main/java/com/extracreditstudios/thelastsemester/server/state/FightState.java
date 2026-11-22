package com.extracreditstudios.thelastsemester.server.state;

import com.extracreditstudios.thelastsemester.shared.model.Match;
import java.util.UUID;

/** The shared Match owns HP, energy and hidden actions; this adds server metadata. */
public class FightState {
    public final UUID matchId = UUID.randomUUID();
    public final Match match;
    public final UUID playerAId;
    public final UUID playerBId;
    public long stateVersion;
    public Long turnDeadlineMillis;
    public Long matchDeadlineMillis;

    /** The player IDs must belong to the supplied match's A/B participants. */
    public FightState(Match match, UUID playerAId, UUID playerBId) {
        this.match = match;
        this.playerAId = playerAId;
        this.playerBId = playerBId;
    }

    /** Call after accepting an action or resolving a turn. */
    public void markChanged() {
        stateVersion++;
    }
}
