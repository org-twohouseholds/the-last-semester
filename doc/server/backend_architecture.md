# Backend architecture

## Tech Stack

- Java HTTP framework
  - ✅Javalin
    - simple
    - free choice of libraries (JSON, DB, hashing, ...)
    - manual wiring of libraries
  - ❌Spring Boot
    - many things included
    - industry standard
    - complex to learn (annotation magic, ...)
    - slower
- Database
  - ❌PostgreSQL
  - ✅MySQL (most experience)
  - ❌MariaDB

## State handling

- RAM (`server.state.GameStateStore`)
  - lobby/tournament (`LobbyState`)
    - ID
    - state version (for polling)
    - players (`PlayerState`)
      - account ID (/ bot flag)
      - last seen (bot takeover)
      - `shared/` (character, perks, CP balance, shop offer, ...)
    - `shared/` tournament bracket (pairings)
    - phase (lobby/fight/shop/finished/...) + deadline
    - running fights (`FightState`)
      - state version (for polling)
      - `shared/` (HP, mana, pending actions)
- Database
  - account
    - ID
    - username
    - password hash
    - session token hash
      - expires at?
    - selected character
    - (trophies)

### Einfache RAM Klassen

Die Klassen unter `server.state` sind zunächst Datenbehälter. Ihre öffentlichen Felder können die Serverhandler direkt befüllen. Sie werden **nicht** an Clients serialisiert; dafür sind die DTOs in `shared.messages.game` vorgesehen.

- `GameStateStore` speichert Lobbys nach ihrer ID und bietet `createLobby`, `getLobby` und `removeLobby`.
- `LobbyState` enthält Spieler, laufende Kämpfe, das gemeinsame `Tournament`, Phase, Deadline und Version. Die Lobby-ID dient zunächst auch als äußere Turnierkennung.
- `PlayerState` enthält die Account-Zuordnung, eine UUID für den Turnierplatz, Name, Bot-Kennzeichen und `lastSeenMillis`. Eine `TournamentParticipation` hält Charakter und Perks. CP und Shopangebote werden vorerst hier gespeichert, weil das Shared-Modell sie noch nicht anbietet. Vor Charakterwahl/Turnierstart darf `participation` noch `null` sein. Bei Botübernahme bleiben Spieler-ID und Participation erhalten; nur die Steuerung wechselt.
- `FightState` verbindet ein bestehendes `Match` mit den UUIDs seiner beiden Spieler, Version und Deadlines. HP, Energie und verdeckte Aktionen bleiben ausschließlich im `Match` beziehungsweise seinen Charakterinstanzen.

Beispiel für die Serverhandler:

```java
GameStateStore state = new GameStateStore(); // einmal beim Serverstart anlegen
LobbyState lobby = state.createLobby(8);
PlayerState player = new PlayerState(42L, "Anna");
lobby.addPlayer(player);
player.characterId = 1;
lobby.markChanged();
```

Ein Bot kann mit `new PlayerState(null, "Bot")` angelegt werden. `addPlayer` lehnt volle Lobbys, doppelte Spieler-UUIDs und Beitritte außerhalb der Lobbyphase ab. `removePlayer` funktioniert ebenfalls nur in der Lobbyphase. Die Zuordnung einer Account-ID zu genau einem Platz muss der Serverhandler prüfen.

`addPlayer` und `removePlayer` erhöhen bei Erfolg die Lobbyversion. Nach direkten Änderungen an Feldern oder Maps ruft der Handler `markChanged()` auf. Nach einer akzeptierten Kampfaktion/Zugauflösung erhöht er sowohl die Kampfversion (`fight.markChanged()`) als auch die Lobbyversion (`lobby.markChanged()`). Reines Polling erhöht keine Version.

Alle Zeitwerte sind Unixzeit in Millisekunden, nicht gesetzte Deadlines sind `null`. `lastSeenMillis` wird vom Handler bei Kontakt aktualisiert. Timerablauf und Botübernahme werden dadurch noch nicht automatisch ausgeführt.

**Nächster Anschluss durch das Serverteam:** Store einmal anlegen, Session einem Account/Spieler zuordnen, Lobby-/Fight-Routen hinzufügen und DTOs befüllen. Der Zugriff auf den Store und die zusammengehörigen Spieländerungen muss dabei mit derselben Sperre erfolgen, zunächst beispielsweise `synchronized (state)`. Die Speicherklassen selbst sind nicht threadsicher. Bracket-Zugriff/-Fortschritt im Shared-Turnier ist noch unvollständig; das bestehende `Tournament` wird hier nur referenziert, nicht neu implementiert.

## Various

- runs as `.jar` on Prepaid-Host server
- reverse proxy in front for TLS (e.g. Caddy)
  - standard
- Docker Compose for server & DB
- DB backups?
