-- Renomme le modèle "T-shirt V-Neck" en "FLORAL PRINT T-SHIRT" et fixe son
-- prix à 35.95. Une seule couleur est conservée : White devient Mint (Black
-- et Red supprimées). Son ancienne photo est remplacée par les 3 vraies
-- photos produit fournies (modèle, face, dos) ; la 1re garde la position 1,
-- les 2 suivantes passent après toutes les autres photos.

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"FLORAL PRINT T-SHIRT"'),
    price = 35.95
WHERE attributes ->> 'model' = 'T-shirt V-Neck';

DELETE FROM product_variants WHERE sku IN ('COLV-NOIR', 'COLV-ROUGE');

UPDATE product_variants
SET sku = 'COLV-MINT',
    attributes = jsonb_set(attributes, '{color}', '"Mint"')
WHERE sku = 'COLV-BLANC';

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'COLV-MINT');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/floral-print-tshirt-mint-model.png', 1),
    ('/products/floral-print-tshirt-mint-front.png', 20),
    ('/products/floral-print-tshirt-mint-back.png', 21)
) AS img(url, position)
WHERE pv.sku = 'COLV-MINT';
