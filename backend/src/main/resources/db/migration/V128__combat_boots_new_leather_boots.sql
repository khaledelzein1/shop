-- "Combat Boots" ne doit exister qu'en une seule couleur (Marron) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "NEW
-- LEATHER BOOTS", fixe le prix à 99.95, renomme la couleur Noir en
-- Marron et remplace sa photo générique par les 3 vraies photos produit
-- fournies (modèle, côté, face).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('BTCB-RED', 'BTCB-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('BTCB-RED', 'BTCB-WHITE');

UPDATE product_variants
SET sku = 'BTCB-BROWN',
    attributes = jsonb_set(jsonb_set(attributes, '{model}', '"NEW LEATHER BOOTS"'), '{color}', '"Brown"'),
    price = 99.95
WHERE sku = 'BTCB-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'BTCB-BROWN');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/new-leather-boots-model.png', 0),
    ('/products/new-leather-boots-side.png', 1),
    ('/products/new-leather-boots-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'BTCB-BROWN';
