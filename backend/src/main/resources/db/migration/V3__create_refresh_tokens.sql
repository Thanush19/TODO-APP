CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY,
                                user_id UUID NOT NULL,
                                token_hash VARCHAR(64) NOT NULL,
                                expires_at TIMESTAMPTZ NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL,
                                revoked_at TIMESTAMPTZ,

                                CONSTRAINT uk_refresh_tokens_token_hash
                                    UNIQUE (token_hash),

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id
    ON refresh_tokens (user_id);

CREATE INDEX idx_refresh_tokens_expires_at
    ON refresh_tokens (expires_at);