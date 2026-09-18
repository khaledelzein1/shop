-- Rotation de refresh token : on ne stocke jamais le token en clair, seulement son
-- empreinte SHA-256, pour qu'une fuite de la base ne permette pas de rejouer les tokens.

CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    user_id     BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT false,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
