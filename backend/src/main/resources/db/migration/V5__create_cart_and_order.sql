-- Panier

CREATE TABLE carts (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE cart_items (
    id                    BIGSERIAL PRIMARY KEY,
    cart_id               BIGINT NOT NULL REFERENCES carts (id) ON DELETE CASCADE,
    variant_id            BIGINT NOT NULL REFERENCES product_variants (id),
    quantity              INTEGER NOT NULL,
    unit_price_snapshot   NUMERIC(10, 2) NOT NULL,
    created_at            TIMESTAMP NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
CREATE UNIQUE INDEX uq_cart_items_cart_variant ON cart_items (cart_id, variant_id);

-- Commandes : document figé, adresse de livraison recopiée (pas de FK vers addresses)

CREATE TABLE orders (
    id                   BIGSERIAL PRIMARY KEY,
    status               VARCHAR(20) NOT NULL,
    total_amount         NUMERIC(10, 2) NOT NULL,
    user_id              BIGINT NOT NULL REFERENCES users (id),
    shipping_label       VARCHAR(150),
    shipping_street      VARCHAR(255) NOT NULL,
    shipping_city        VARCHAR(150) NOT NULL,
    shipping_zip_code    VARCHAR(20) NOT NULL,
    shipping_country     VARCHAR(100) NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_user_id ON orders (user_id);

CREATE TABLE order_items (
    id             BIGSERIAL PRIMARY KEY,
    order_id       BIGINT NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    variant_id     BIGINT REFERENCES product_variants (id) ON DELETE SET NULL,
    product_name   VARCHAR(255) NOT NULL,
    sku            VARCHAR(100) NOT NULL,
    quantity       INTEGER NOT NULL,
    unit_price     NUMERIC(10, 2) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
