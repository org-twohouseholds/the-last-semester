# Authentication Design

- HTTPS
- accounts
  - username: unique (case-insensitive), 3-20 characters
  - password: min. 15 characters
    - stored as salted hash (random salt, Argon2id?)
  - (rate-limit failed logins)
- login -> session token (random)
  - client sends with every request (for authentication)
    - as `Authorization: Bearer <session_token>` header (standard → redacted in proxy logs, never cached)
  - server stores hash in database
  - deleted on logout
  - expires? (30d?)
- (Spring Boot Security?)
