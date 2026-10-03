-- "Slim Fit Trousers" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "WOOL
-- tuxedo-style SUIT TROUSERS", fixe le prix à 119 et remplace la photo
-- générique par les 3 vraies photos produit fournies (modèle, face,
-- dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'SLT-RED' OR sku LIKE 'SLT-WHITE');

DELETE FROM product_variants
WHERE sku IN ('SLT-RED', 'SLT-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"WOOL tuxedo-style SUIT TROUSERS"'),
    price = 119
WHERE sku = 'SLT-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SLT-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/wool-tuxedo-trousers-model.png', 0),
    ('/products/wool-tuxedo-trousers-front.png', 1),
    ('/products/wool-tuxedo-trousers-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SLT-BLACK';
