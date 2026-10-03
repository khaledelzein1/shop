-- Renomme "Leather Bomber Jacket" (Noir) en "REGULAR FIT LEATHER JACKET",
-- fixe le prix à 149 et remplace la photo générique par les 2 vraies
-- photos produit fournies (face, dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"REGULAR FIT LEATHER JACKET"'),
    price = 149
WHERE sku LIKE 'LTBM-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBM-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-regular-front.png', 0),
    ('/products/jackets/leather-regular-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'LTBM-BLACK-%';
