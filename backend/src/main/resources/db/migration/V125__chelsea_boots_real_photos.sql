-- Renomme "Chelsea Boots" en "CHELSEA BOOTS", fixe le prix à 49.95 pour
-- toutes les couleurs et remplace la photo générique de la couleur Noir
-- par les 3 vraies photos produit fournies (modèle, côté, face).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"CHELSEA BOOTS"'),
    price = 49.95
WHERE sku LIKE 'BTC-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'BTC-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/chelsea-boots-model.png', 0),
    ('/products/chelsea-boots-side.png', 1),
    ('/products/chelsea-boots-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'BTC-BLACK';
