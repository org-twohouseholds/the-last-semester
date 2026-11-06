CREATE TABLE account (
  id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  username              VARCHAR(20)  NOT NULL UNIQUE,
  password_hash         VARCHAR(255) NOT NULL,
  session_token_hash    CHAR(64)     NULL UNIQUE,
  selected_character_id INT          NULL
);
