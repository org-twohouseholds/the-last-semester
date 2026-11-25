# REST API interfaces

## Use cases, `API`, example input & output

### Account/Auth

- register
  - `POST /api/auth/register`
  - input: header w/ protocol version & `{ username, password }`
  - output: `{ session_token }`
- login
  - `POST /api/auth/login`
  - input: header w/ protocol version & `{ username, password }`
  - output: `{ session_token }`
- logout
  - `POST /api/auth/logout`
  - input: header w/ protocol version & session token
  - output: HTTP status code
- delete account
  - `POST /api/account/delete` (DELETE poorly supports body)
  - input: header w/ protocol version & session token + `{ password }`
  - output: HTTP status code
- delete account with username and password
  - `POST /api/account/delete-with-credentials`
  - Send the `protocol-version` header and `{ username, password }` in the JSON body.
  - Returns `204 No Content` on success.
- change password
  - `PUT /api/account/password`
  - input: header w/ protocol version & session token + `{ old_password, new_password }`
  - output: HTTP status code
- ❓get username (& check session_token validity)
  - `GET /api/account`
  - input: header w/ protocol version & session token
  - output: `{ username }`
- (get trophies)
- (update settings)

### Characters

- select character
  - `POST /api/character/select`
  - input: header w/ protocol version & session token + `{ character_id }`
  - output: HTTP status code
- **NOT** view all/specific character (client-side)

### Tournament (Lobby & Fight)

- join lobby
  - `POST /api/lobby/join`
  - input: header w/ protocol version & session token
  - output: HTTP status code
- quit lobby
  - `POST /api/lobby/quit`
  - input: header w/ protocol version & session token
  - output: HTTP status code
- get state (polling)
  - `GET /api/game`
  - input: header w/ protocol version & session token
  - output: `{ phase, ... }`
- select action
  - `POST /api/fight/select`
  - input: header w/ protocol version & session token + `{ tournament, fight, turn, actionIndex }`
  - output: HTTP status code
- (create lobby)
- (kick lobby member)

### Shop

- choose perk
  - `POST /api/shop/buy`
  - input: header w/ protocol version & session token + `{ tournament, fight, perkIndex }`
  - output: HTTP status code
- (re-roll perks)

### Open questions

- error codes -> meanings
- indices -> meanings
- `phase` values -> meanings & content
