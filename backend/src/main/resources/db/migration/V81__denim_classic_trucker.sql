-- Renomme "Denim Jacket Classic" en "TRUCKER DENIM JACKET", fixe le prix
-- à 119 pour toutes les couleurs, renomme la couleur Blanc en Bleu et
-- remplace sa photo générique par les 3 vraies photos produit fournies
-- (modèle, face, dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"TRUCKER DENIM JACKET"'),
    price = 119
WHERE sku LIKE 'DNC-%';

UPDATE product_variants
SET sku = replace(sku, 'DNC-WHITE-', 'DNC-BLUE-'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku LIKE 'DNC-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNC-BLUE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/trucker-denim-blue-model.png', 0),
    ('/products/jackets/trucker-denim-blue-front.png', 1),
    ('/products/jackets/trucker-denim-blue-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'DNC-BLUE-%';
