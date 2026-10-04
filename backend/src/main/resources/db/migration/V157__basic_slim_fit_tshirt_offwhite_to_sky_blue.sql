-- "BASIC SLIM FIT T-SHIRT" : la couleur Off-White (photo beige) est
-- remplacée par Sky Blue. Ses 2 anciennes photos sont supprimées et
-- remplacées par les 3 vraies photos produit fournies (modèle, face, dos),
-- en positions 0 à 2 pour rester la 1re couleur affichée. Renommage en
-- place : la variante garde son id.

UPDATE product_variants
SET sku = 'ESS-SKYBLUE',
    attributes = jsonb_set(attributes, '{color}', '"Sky Blue"')
WHERE sku = 'ESS-OFFWHITE';

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-SKYBLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-slim-fit-tshirt-sky-blue-model.png', 0),
    ('/products/basic-slim-fit-tshirt-sky-blue-front.png', 1),
    ('/products/basic-slim-fit-tshirt-sky-blue-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'ESS-SKYBLUE';
