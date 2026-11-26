CREATE TABLE account (
  id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  username              VARCHAR(20)  NOT NULL,
  password_hash         VARCHAR(255) NOT NULL,
  session_token_hash    CHAR(64)     NULL,
  selected_character_id INT          NULL,
  CONSTRAINT uq_account_username UNIQUE(username),
  CONSTRAINT uq_account_session_token_hash UNIQUE(session_token_hash)
);
