CREATE TABLE addresses (
    id          BIGSERIAL PRIMARY KEY,
    label       VARCHAR(150),
    street      VARCHAR(255) NOT NULL,
    city        VARCHAR(150) NOT NULL,
    zip_code    VARCHAR(20) NOT NULL,
    country     VARCHAR(100) NOT NULL,
    is_default  BOOLEAN NOT NULL DEFAULT false,
    user_id     BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);
