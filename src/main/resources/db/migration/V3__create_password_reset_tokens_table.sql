CREATE TABLE password_reset_tokens (
    id           BINARY(16)   NOT NULL,
    user_id      BINARY(16)   NOT NULL,
    token_hash   VARCHAR(64)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    expires_at   DATETIME(6)  NOT NULL,
    used_at      DATETIME(6),
    CONSTRAINT pk_password_reset_tokens   PRIMARY KEY (id),
    CONSTRAINT uk_prt_token_hash          UNIQUE (token_hash),
    CONSTRAINT fk_prt_user                FOREIGN KEY (user_id) REFERENCES users(id)
);
