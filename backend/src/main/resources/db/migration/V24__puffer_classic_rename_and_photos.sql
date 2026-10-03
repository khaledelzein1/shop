-- Renomme "Puffer Jacket Classic" en "LIGHTWEIGHT WATER-REPELLENT PADDED
-- JACKET" (toutes couleurs/tailles) et remplace la photo des 6 variantes
-- Noires par les 3 vraies photos produit fournies (face, dos, détail
-- zip) — Blanc et Rouge gardent leur photo générique existante.

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LIGHTWEIGHT WATER-REPELLENT PADDED JACKET"')
WHERE sku LIKE 'PFC-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFC-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/lightweight-padded-black-front.png', 0),
    ('/products/jackets/lightweight-padded-black-back.png', 1),
    ('/products/jackets/lightweight-padded-black-detail.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFC-BLACK-%';
