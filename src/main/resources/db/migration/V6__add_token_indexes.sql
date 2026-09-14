-- Index on refresh_tokens.user_id speeds up revocation queries per user
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

-- Index on password_reset_tokens.user_id speeds up invalidateAllForUser
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
