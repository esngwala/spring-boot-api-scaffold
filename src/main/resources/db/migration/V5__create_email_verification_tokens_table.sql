CREATE TABLE email_verification_tokens (
    id           BINARY(16)   NOT NULL,
    user_id      BINARY(16)   NOT NULL,
    token_hash   VARCHAR(64)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    expires_at   DATETIME(6)  NOT NULL,
    used_at      DATETIME(6),
    CONSTRAINT pk_email_verification_tokens  PRIMARY KEY (id),
    CONSTRAINT uk_evt_token_hash             UNIQUE (token_hash),
    CONSTRAINT fk_evt_user                   FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_evt_user_id ON email_verification_tokens (user_id);
