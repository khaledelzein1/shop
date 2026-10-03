-- Renomme "Regular Fit Chino" en "BASIC SKINNY JEANS", fixe le prix à
-- 25.99 pour toutes les couleurs et remplace la photo générique de la
-- couleur Noir par les 3 vraies photos produit fournies (modèle, face,
-- dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"BASIC SKINNY JEANS"'),
    price = 25.99
WHERE sku LIKE 'RGC-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGC-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-skinny-jeans-model.png', 0),
    ('/products/basic-skinny-jeans-front.png', 1),
    ('/products/basic-skinny-jeans-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'RGC-BLACK';
