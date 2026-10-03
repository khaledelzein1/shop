-- "High-Top Sneakers" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "NEW RETRO
-- STYLE TRAINERS", fixe le prix à 29.95 et remplace sa photo générique
-- par les 3 vraies photos produit fournies (modèle, côté, face).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SNKH-RED', 'SNKH-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SNKH-RED', 'SNKH-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"NEW RETRO STYLE TRAINERS"'),
    price = 29.95
WHERE sku = 'SNKH-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SNKH-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/new-retro-style-trainers-model.png', 0),
    ('/products/new-retro-style-trainers-side.png', 1),
    ('/products/new-retro-style-trainers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SNKH-BLACK';
