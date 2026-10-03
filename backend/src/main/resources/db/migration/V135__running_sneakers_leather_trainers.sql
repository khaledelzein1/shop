-- "Running Sneakers" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "LEATHER
-- TRAINERS", fixe le prix à 59.95 et remplace sa photo générique par les
-- 3 vraies photos produit fournies (modèle, côté, face).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SNKR-RED', 'SNKR-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SNKR-RED', 'SNKR-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LEATHER TRAINERS"'),
    price = 59.95
WHERE sku = 'SNKR-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SNKR-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/leather-trainers-model.png', 0),
    ('/products/leather-trainers-side.png', 1),
    ('/products/leather-trainers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SNKR-BLACK';
