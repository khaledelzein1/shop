-- Renomme le modèle "T-shirt Essential" en "BASIC SLIM FIT T-SHIRT" et fixe
-- son prix à 12.95 (toutes couleurs). La couleur White reçoit les 3 vraies
-- photos produit fournies (modèle, face, dos) en tête ; sa photo existante
-- est conservée après elles. Aucune photo n'est supprimée.

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"BASIC SLIM FIT T-SHIRT"'),
    price = 12.95
WHERE attributes ->> 'model' = 'T-shirt Essential';

UPDATE product_images
SET position = 5
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-BLANC');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-slim-fit-tshirt-white-model.png', 2),
    ('/products/basic-slim-fit-tshirt-white-front.png', 3),
    ('/products/basic-slim-fit-tshirt-white-back.png', 4)
) AS img(url, position)
WHERE pv.sku = 'ESS-BLANC';
