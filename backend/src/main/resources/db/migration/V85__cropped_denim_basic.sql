-- "Cropped Denim Jacket" (4e modèle Denim) ne doit exister qu'en une
-- seule couleur (Noir) : supprime les couleurs Rouge et Blanc, renomme
-- le modèle en "BASIC DENIM JACKET", fixe le prix à 35.95 et remplace la
-- photo générique par les 3 vraies photos produit fournies (modèle,
-- face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNCR-RED-%' OR sku LIKE 'DNCR-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'DNCR-RED-%' OR sku LIKE 'DNCR-WHITE-%';

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"BASIC DENIM JACKET"'),
    price = 35.95
WHERE sku LIKE 'DNCR-BLACK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNCR-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/basic-denim-model.png', 0),
    ('/products/jackets/basic-denim-front.png', 1),
    ('/products/jackets/basic-denim-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'DNCR-BLACK-%';
