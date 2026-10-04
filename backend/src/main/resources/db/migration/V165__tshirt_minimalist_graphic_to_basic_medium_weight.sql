-- Renomme le modèle "T-shirt Minimalist Graphic" en "BASIC MEDIUM WEIGHT
-- T-SHIRT" et fixe son prix à 17.95. Seule la couleur White est conservée
-- (Black et Red supprimées). Son ancienne photo est remplacée par les 3
-- vraies photos produit fournies (modèle, face, dos) ; la 1re garde la
-- position 1, les 2 suivantes passent après toutes les autres photos.

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"BASIC MEDIUM WEIGHT T-SHIRT"'),
    price = 17.95
WHERE attributes ->> 'model' = 'T-shirt Minimalist Graphic';

DELETE FROM product_variants WHERE sku IN ('GRPH-NOIR', 'GRPH-ROUGE');

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'GRPH-BLANC');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-medium-weight-tshirt-white-model.png', 1),
    ('/products/basic-medium-weight-tshirt-white-front.png', 18),
    ('/products/basic-medium-weight-tshirt-white-back.png', 19)
) AS img(url, position)
WHERE pv.sku = 'GRPH-BLANC';
