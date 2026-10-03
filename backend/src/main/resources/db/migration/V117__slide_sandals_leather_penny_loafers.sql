-- Renomme "Slide Sandals" en "LEATHER PENNY LOAFERS", fixe le prix à
-- 59.95 pour toutes les couleurs, renomme la couleur Noir en Marron et
-- remplace sa photo générique par les 3 vraies photos produit fournies
-- (modèle, côté, face).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LEATHER PENNY LOAFERS"'),
    price = 59.95
WHERE sku LIKE 'SDS-%';

UPDATE product_variants
SET sku = replace(sku, 'SDS-BLACK', 'SDS-BROWN'),
    attributes = jsonb_set(attributes, '{color}', '"Brown"')
WHERE sku = 'SDS-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SDS-BROWN');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/leather-penny-loafers-model.png', 0),
    ('/products/leather-penny-loafers-side.png', 1),
    ('/products/leather-penny-loafers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SDS-BROWN';
