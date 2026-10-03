-- "Classic Sneakers" ne doit exister qu'en une seule couleur (Marron) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "RETRO-STYLE
-- TRAINERS", fixe le prix à 20.95, renomme la couleur Noir en Marron et
-- remplace sa photo générique par les 3 vraies photos produit fournies
-- (modèle, côté, face). La photo modèle reprend le rôle de photo
-- principale du produit Shoes, que portait la photo générique supprimée.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SNK-RED', 'SNK-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SNK-RED', 'SNK-WHITE');

UPDATE product_variants
SET sku = 'SNK-BROWN',
    attributes = jsonb_set(jsonb_set(attributes, '{model}', '"RETRO-STYLE TRAINERS"'), '{color}', '"Brown"'),
    price = 20.95
WHERE sku = 'SNK-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SNK-BROWN');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, (img.position = 0), pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/retro-style-trainers-model.png', 0),
    ('/products/retro-style-trainers-side.png', 1),
    ('/products/retro-style-trainers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SNK-BROWN';
