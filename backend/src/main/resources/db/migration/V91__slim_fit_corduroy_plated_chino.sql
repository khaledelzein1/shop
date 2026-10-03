-- Renomme "Slim Fit Corduroy" en "PLATED RELAXED FIT CHINO TROUSERS",
-- fixe le prix à 49.95 pour toutes les couleurs et remplace la photo
-- générique de la couleur Noir par les 3 vraies photos produit fournies
-- (modèle, face, dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"PLATED RELAXED FIT CHINO TROUSERS"'),
    price = 49.95
WHERE sku LIKE 'SLCD-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SLCD-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/plated-relaxed-chino-model.png', 0),
    ('/products/plated-relaxed-chino-front.png', 1),
    ('/products/plated-relaxed-chino-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SLCD-BLACK';
