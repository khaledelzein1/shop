-- "Leather Biker Jacket" ne doit exister qu'en une seule couleur (Noir).
-- Supprime les variantes Rouge et Blanc, renomme le modèle en
-- "CROPPED FIT LEATHER BIKER JACKET", fixe le prix à 199 et remplace
-- la photo générique par les 2 vraies photos produit fournies (face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTB-RED-%' OR sku LIKE 'LTB-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'LTB-RED-%' OR sku LIKE 'LTB-WHITE-%';

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"CROPPED FIT LEATHER BIKER JACKET"'),
    price = 199
WHERE sku LIKE 'LTB-BLACK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTB-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-biker-front.png', 0),
    ('/products/jackets/leather-biker-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'LTB-BLACK-%';
