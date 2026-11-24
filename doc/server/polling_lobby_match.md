# Tasks

- Join/quit lobby (connect 2 players in a lobby/tournament)
- Polling (synchronize fight states)

## Flow

1. 🟠Join lobby (POST)
2. 🔵Create a lobby if no lobby has space left (game phase: LOBBY)
3. 🟠
    - Join lobby (🔵add)
    - Leave lobby (🔵remove)
    - Lobby polling (🔵send state)
4. 🔵Fill up with bots after the deadline
5. Loop (matches):
    1. 🟠Lobby polling
    2. 🔵Create matches (game phase: FIGHT, match status: IN_PROGRESS)
    3. Loop (turns):
        1. 🟠Select actions
        2. 🔵Validate actions (submitAbility() -> store actions)
        3. 🟠Match polling (🔵send state)
        4. 🔵Both sides selected early -> resolve immediately
        5. 🔵End action selection after the timer
        6. 🔵Resolve actions (resolveTurn())
        7. (🔵Let early winners into the shop already?)
    4. 🔵End matches after the timer (match status: FINISHED)
    5. 🟠winnerId == myId?
        - yes -> game phase?
            - SHOP
                - 🔵Award CP
                - 🟠Select perk
                - 🔵End shop phase (game phase: FIGHT) -> "continue"
            - FINISHED (winning screen) -> "break"
        - no (losing screen) -> "break"

### Legend

- 🟠: Client
- 🔵: Server

## Protocol

1. Client: game state? -> clientStateVersion (lobby/tournament & match)
2. Server:
    - If serverStateVersion == clientStateVersion -> null
    - If serverStateVersion > clientStateVersion -> new state

## APIs

- lobby polling
- match polling
