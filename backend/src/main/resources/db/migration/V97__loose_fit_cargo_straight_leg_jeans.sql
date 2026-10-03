-- Renomme "Loose Fit Cargo" en "STRAIGHT-LEG JEANS", fixe le prix à
-- 29.99 pour toutes les couleurs, renomme la couleur Blanc en Bleu et
-- remplace sa photo générique par les 3 vraies photos produit fournies
-- (modèle, face, dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"STRAIGHT-LEG JEANS"'),
    price = 29.99
WHERE sku LIKE 'LSCG-%';

UPDATE product_variants
SET sku = replace(sku, 'LSCG-WHITE', 'LSCG-BLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku = 'LSCG-WHITE';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'LSCG-BLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/straight-leg-jeans-model.png', 0),
    ('/products/straight-leg-jeans-front.png', 1),
    ('/products/straight-leg-jeans-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'LSCG-BLUE';
