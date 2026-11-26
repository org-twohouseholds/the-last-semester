package com.extracreditstudios.thelastsemester.shared.model;

/** Contract for everything that can fight in a match: it must provide its fighter. */
public interface Combatant {

    CharacterInstance getCharacter();
}
