-- "Strap Sandals" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "LEATHER
-- TRACK SOLE LOAFERS", fixe le prix à 59.95 et remplace la photo
-- générique par les 3 vraies photos produit fournies (modèle, côté,
-- face).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SDST-RED', 'SDST-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SDST-RED', 'SDST-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LEATHER TRACK SOLE LOAFERS"'),
    price = 59.95
WHERE sku = 'SDST-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SDST-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/leather-track-sole-loafers-model.png', 0),
    ('/products/leather-track-sole-loafers-side.png', 1),
    ('/products/leather-track-sole-loafers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SDST-BLACK';
