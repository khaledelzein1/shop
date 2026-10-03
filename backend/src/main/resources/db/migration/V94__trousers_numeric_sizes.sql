-- Ajoute des tailles numériques (36/38/40/42/44/46, comme Zara) à tous
-- les modèles du champ Trousers.

-- WOOL SUIT TROUSERS a déjà des tailles lettres (XS..XXL) : on les
-- renomme en place en tailles numériques (les variantes ne changent pas
-- d'id, donc les paniers existants restent valides).
UPDATE product_variants SET sku = replace(sku, '-XS', '-36'), attributes = jsonb_set(attributes, '{size}', '"36"') WHERE sku = 'SLJ-BLACK-XS';
UPDATE product_variants SET sku = replace(sku, '-S', '-38'), attributes = jsonb_set(attributes, '{size}', '"38"') WHERE sku = 'SLJ-BLACK-S';
UPDATE product_variants SET sku = replace(sku, '-M', '-40'), attributes = jsonb_set(attributes, '{size}', '"40"') WHERE sku = 'SLJ-BLACK-M';
UPDATE product_variants SET sku = replace(sku, '-L', '-42'), attributes = jsonb_set(attributes, '{size}', '"42"') WHERE sku = 'SLJ-BLACK-L';
UPDATE product_variants SET sku = replace(sku, '-XL', '-44'), attributes = jsonb_set(attributes, '{size}', '"44"') WHERE sku = 'SLJ-BLACK-XL';
UPDATE product_variants SET sku = replace(sku, '-XXL', '-46'), attributes = jsonb_set(attributes, '{size}', '"46"') WHERE sku = 'SLJ-BLACK-XXL';

-- WOOL tuxedo-style SUIT TROUSERS, PLATED RELAXED FIT CHINO TROUSERS et
-- RELAXED FIT CARGO TROUSERS n'ont encore qu'une seule variante sans
-- taille chacun : on les multiplie en 6 tailles. Une ligne de panier
-- pointant vers l'ancienne variante sans taille est migrée vers la
-- nouvelle taille 40 plutôt que d'être perdue.

WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'SLT-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'SLT-BLACK-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items
SET variant_id = (SELECT id FROM new_variants WHERE sku = 'SLT-BLACK-40')
WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'SLT-BLACK')) AS pi
WHERE pv.sku LIKE 'SLT-BLACK-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'SLT-BLACK');
DELETE FROM product_variants WHERE sku = 'SLT-BLACK';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'SLCD-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'SLCD-BLACK-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items
SET variant_id = (SELECT id FROM new_variants WHERE sku = 'SLCD-BLACK-40')
WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'SLCD-BLACK')) AS pi
WHERE pv.sku LIKE 'SLCD-BLACK-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'SLCD-BLACK');
DELETE FROM product_variants WHERE sku = 'SLCD-BLACK';


WITH old_variant AS (
    SELECT id, product_id, price, attributes FROM product_variants WHERE sku = 'SLTK-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT 'SLTK-BLACK-' || sizes.size, old_variant.price, 20, true, old_variant.attributes || jsonb_build_object('size', sizes.size), old_variant.product_id, now(), now()
    FROM old_variant
    CROSS JOIN (VALUES ('36'), ('38'), ('40'), ('42'), ('44'), ('46')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items
SET variant_id = (SELECT id FROM new_variants WHERE sku = 'SLTK-BLACK-40')
WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (SELECT url, position FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'SLTK-BLACK')) AS pi
WHERE pv.sku LIKE 'SLTK-BLACK-%';

DELETE FROM product_images WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'SLTK-BLACK');
DELETE FROM product_variants WHERE sku = 'SLTK-BLACK';
