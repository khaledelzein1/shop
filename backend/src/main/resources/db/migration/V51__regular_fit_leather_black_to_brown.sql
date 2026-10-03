-- Le cuir de la veste REGULAR FIT LEATHER JACKET est en réalité marron
-- foncé, pas noir : corrige la couleur et remplace la photo générique par
-- les 2 vraies photos produit fournies (face, dos).

UPDATE product_variants
SET sku = replace(sku, 'LTBM-BLACK-', 'LTBM-BROWN-'),
    attributes = jsonb_set(attributes, '{color}', '"Brown"')
WHERE sku LIKE 'LTBM-BLACK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBM-BROWN-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-regular-brown-front.png', 0),
    ('/products/jackets/leather-regular-brown-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'LTBM-BROWN-%';
