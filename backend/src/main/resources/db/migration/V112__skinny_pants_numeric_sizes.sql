-- Ajoute des tailles numériques (36/38/40/42/44/46, comme le champ
-- Trousers) à tous les modèles du champ Pants, section SKINNY. Chaque
-- variante sans taille est multipliée en 6 tailles ; une ligne de
-- panier pointant vers l'ancienne variante est migrée vers la nouvelle
-- taille 40 plutôt que d'être perdue.

WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGC-BLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGC-BLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGC-BLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGC-BLUE')) AS pi
WHERE pv.sku LIKE 'RGC-BLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGC-BLUE');
DELETE FROM product_variants WHERE sku = 'RGC-BLUE';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGC-DARKBLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGC-DARKBLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGC-DARKBLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGC-DARKBLUE')) AS pi
WHERE pv.sku LIKE 'RGC-DARKBLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGC-DARKBLUE');
DELETE FROM product_variants WHERE sku = 'RGC-DARKBLUE';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGTK-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGTK-BLACK-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGTK-BLACK-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGTK-BLACK')) AS pi
WHERE pv.sku LIKE 'RGTK-BLACK-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGTK-BLACK');
DELETE FROM product_variants WHERE sku = 'RGTK-BLACK';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGTK-BLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGTK-BLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGTK-BLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGTK-BLUE')) AS pi
WHERE pv.sku LIKE 'RGTK-BLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGTK-BLUE');
DELETE FROM product_variants WHERE sku = 'RGTK-BLUE';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGTK-LIGHTBLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGTK-LIGHTBLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGTK-LIGHTBLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGTK-LIGHTBLUE')) AS pi
WHERE pv.sku LIKE 'RGTK-LIGHTBLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGTK-LIGHTBLUE');
DELETE FROM product_variants WHERE sku = 'RGTK-LIGHTBLUE';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGST-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGST-BLACK-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGST-BLACK-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGST-BLACK')) AS pi
WHERE pv.sku LIKE 'RGST-BLACK-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGST-BLACK');
DELETE FROM product_variants WHERE sku = 'RGST-BLACK';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'RGST-BLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'RGST-BLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'RGST-BLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGST-BLUE')) AS pi
WHERE pv.sku LIKE 'RGST-BLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'RGST-BLUE');
DELETE FROM product_variants WHERE sku = 'RGST-BLUE';


-- Champ Pants, section Straight : même traitement.

WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'LSWD-BLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'LSWD-BLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'LSWD-BLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSWD-BLUE')) AS pi
WHERE pv.sku LIKE 'LSWD-BLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSWD-BLUE');
DELETE FROM product_variants WHERE sku = 'LSWD-BLUE';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'LSCG-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'LSCG-BLACK-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'LSCG-BLACK-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSCG-BLACK')) AS pi
WHERE pv.sku LIKE 'LSCG-BLACK-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSCG-BLACK');
DELETE FROM product_variants WHERE sku = 'LSCG-BLACK';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'LSCG-BLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'LSCG-BLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'LSCG-BLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSCG-BLUE')) AS pi
WHERE pv.sku LIKE 'LSCG-BLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSCG-BLUE');
DELETE FROM product_variants WHERE sku = 'LSCG-BLUE';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'LSCG-DARKBLUE'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'LSCG-DARKBLUE-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items SET variant_id = (SELECT id FROM new_variants WHERE sku = 'LSCG-DARKBLUE-40') WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSCG-DARKBLUE')) AS pi
WHERE pv.sku LIKE 'LSCG-DARKBLUE-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'LSCG-DARKBLUE');
DELETE FROM product_variants WHERE sku = 'LSCG-DARKBLUE';
