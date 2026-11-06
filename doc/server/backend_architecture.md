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

- RAM
  - lobby
    - ID
    - state version (for polling)
    - players
      - account ID (/ bot flag)
      - last seen (bot takeover)
      - `shared/` (character, perks, CP balance, shop offer, ...)
    - `shared/` tournament bracket (pairings)
    - phase (lobby/fight/shop/finished/...) + deadline
    - running fights
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

## Various

- runs as `.jar` on Prepaid-Host server
- reverse proxy in front for TLS (e.g. Caddy)
  - standard
- Docker Compose for server & DB
- DB backups?
