-- Renomme le modèle "T-shirt Oversize" en "RINGER BASIC CONTRAST RIBBED
-- T-SHIRT" et fixe son prix à 17.95. Seule la couleur White est conservée
-- (Black et Red supprimées). Son ancienne photo est remplacée par les 3
-- vraies photos produit fournies (modèle, face, dos) ; la 1re garde la
-- position 1, les 2 suivantes passent après toutes les autres photos.

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"RINGER BASIC CONTRAST RIBBED T-SHIRT"'),
    price = 17.95
WHERE attributes ->> 'model' = 'T-shirt Oversize';

DELETE FROM product_variants WHERE sku IN ('OVS-NOIR', 'OVS-ROUGE');

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'OVS-BLANC');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/ringer-contrast-ribbed-tshirt-white-model.png', 1),
    ('/products/ringer-contrast-ribbed-tshirt-white-front.png', 10),
    ('/products/ringer-contrast-ribbed-tshirt-white-back.png', 11)
) AS img(url, position)
WHERE pv.sku = 'OVS-BLANC';
