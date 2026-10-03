-- Ajoute des pointures (38 à 45) à tous les modèles/couleurs du rayon
-- Shoes : chaque variante sans taille est démultipliée en 8 variantes
-- (sku + '-38' ... '-45', stock 20), avec ses photos recopiées. Même
-- principe que V94 pour les pantalons : une ligne de panier pointant vers
-- l'ancienne variante est migrée vers la pointure 42 plutôt que perdue ;
-- les lignes de commande gardent leur sku/prix figés (variant_id passe à
-- NULL via ON DELETE SET NULL).

CREATE TEMP TABLE shoe_old_variants ON COMMIT DROP AS
SELECT pv.id, pv.sku, pv.price, pv.attributes, pv.product_id
FROM product_variants pv
JOIN products p ON p.id = pv.product_id
WHERE p.slug = 'shoes' AND NOT (pv.attributes ? 'size');

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT o.sku || '-' || sizes.size, o.price, 20, true,
       o.attributes || jsonb_build_object('size', sizes.size), o.product_id, now(), now()
FROM shoe_old_variants o
CROSS JOIN (VALUES ('38'), ('39'), ('40'), ('41'), ('42'), ('43'), ('44'), ('45')) AS sizes(size);

-- La photo principale du produit (portée par une seule variante) n'est
-- conservée comme principale que sur la pointure 38.
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, (pi.is_primary AND nv.attributes ->> 'size' = '38'), nv.product_id, nv.id, now(), now()
FROM shoe_old_variants o
JOIN product_images pi ON pi.variant_id = o.id
JOIN product_variants nv ON nv.sku = o.sku || '-' || (nv.attributes ->> 'size');

UPDATE cart_items ci
SET variant_id = nv.id
FROM shoe_old_variants o
JOIN product_variants nv ON nv.sku = o.sku || '-42'
WHERE ci.variant_id = o.id;

DELETE FROM product_images WHERE variant_id IN (SELECT id FROM shoe_old_variants);
DELETE FROM product_variants WHERE id IN (SELECT id FROM shoe_old_variants);
