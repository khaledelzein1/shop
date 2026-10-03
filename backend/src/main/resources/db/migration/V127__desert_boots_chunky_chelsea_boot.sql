-- "Desert Boots" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "CHUNKY
-- CHELSEA BOOT", fixe le prix à 55.95 et remplace la photo générique
-- par les 3 vraies photos produit fournies (modèle, côté, face).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('BTD-RED', 'BTD-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('BTD-RED', 'BTD-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"CHUNKY CHELSEA BOOT"'),
    price = 55.95
WHERE sku = 'BTD-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'BTD-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/chunky-chelsea-boot-model.png', 0),
    ('/products/chunky-chelsea-boot-side.png', 1),
    ('/products/chunky-chelsea-boot-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'BTD-BLACK';
