-- "Wide Leg Trousers" ne doit exister qu'en une seule couleur (Bleu,
-- renommée depuis Noir) : supprime les couleurs Rouge et Blanc, renomme
-- le modèle en "RIPPED STRAIGHT-LEG JEANS", fixe le prix à 39.99 et
-- remplace la photo générique par les 3 vraies photos produit fournies
-- (modèle, face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('LSWD-RED', 'LSWD-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('LSWD-RED', 'LSWD-WHITE');

UPDATE product_variants
SET sku = replace(sku, 'LSWD-BLACK', 'LSWD-BLUE'),
    attributes = jsonb_set(jsonb_set(attributes, '{model}', '"RIPPED STRAIGHT-LEG JEANS"'), '{color}', '"Blue"'),
    price = 39.99
WHERE sku = 'LSWD-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'LSWD-BLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/ripped-straight-leg-jeans-model.png', 0),
    ('/products/ripped-straight-leg-jeans-front.png', 1),
    ('/products/ripped-straight-leg-jeans-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'LSWD-BLUE';
