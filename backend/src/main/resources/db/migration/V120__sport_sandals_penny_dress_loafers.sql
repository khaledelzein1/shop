-- "Sport Sandals" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "PENNY
-- DRESS LOAFERS", fixe le prix à 39.95 et remplace la photo générique
-- par les 3 vraies photos produit fournies (modèle, côté, face).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SDSP-RED', 'SDSP-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SDSP-RED', 'SDSP-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"PENNY DRESS LOAFERS"'),
    price = 39.95
WHERE sku = 'SDSP-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SDSP-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/penny-dress-loafers-model.png', 0),
    ('/products/penny-dress-loafers-side.png', 1),
    ('/products/penny-dress-loafers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SDSP-BLACK';
