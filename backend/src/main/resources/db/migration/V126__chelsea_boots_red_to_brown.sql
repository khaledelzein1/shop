-- Renomme la couleur Rouge de CHELSEA BOOTS en Marron et remplace la
-- photo générique par les 3 vraies photos produit fournies (modèle,
-- côté, face).

UPDATE product_variants
SET sku = replace(sku, 'BTC-RED', 'BTC-BROWN'),
    attributes = jsonb_set(attributes, '{color}', '"Brown"')
WHERE sku = 'BTC-RED';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'BTC-BROWN');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/chelsea-boots-brown-model.png', 0),
    ('/products/chelsea-boots-brown-side.png', 1),
    ('/products/chelsea-boots-brown-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'BTC-BROWN';
