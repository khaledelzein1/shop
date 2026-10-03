-- "T-shirt Long Sleeve" (section Long Sleeve) ne doit exister qu'en une
-- seule couleur (Blanc) : supprime les couleurs Noir et Rouge, renomme le
-- modèle en "LONG SLEEVE HENELY T-SHIRT", fixe le prix à 35.95 et
-- remplace sa photo par les 3 vraies photos produit fournies (modèle,
-- face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('MLNG-NOIR', 'MLNG-ROUGE'));

DELETE FROM product_variants
WHERE sku IN ('MLNG-NOIR', 'MLNG-ROUGE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LONG SLEEVE HENELY T-SHIRT"'),
    price = 35.95
WHERE sku = 'MLNG-BLANC';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'MLNG-BLANC');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/long-sleeve-henley-tshirt-model.png', 0),
    ('/products/long-sleeve-henley-tshirt-front.png', 1),
    ('/products/long-sleeve-henley-tshirt-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MLNG-BLANC';
