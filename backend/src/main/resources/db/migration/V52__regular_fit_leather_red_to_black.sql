-- Renomme la couleur Rouge de REGULAR FIT LEATHER JACKET en Noir et
-- remplace la photo générique par les 2 vraies photos produit fournies
-- (face, dos).

UPDATE product_variants
SET sku = replace(sku, 'LTBM-RED-', 'LTBM-BLACK-'),
    attributes = jsonb_set(attributes, '{color}', '"Black"')
WHERE sku LIKE 'LTBM-RED-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBM-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-regular-black-front.png', 0),
    ('/products/jackets/leather-regular-black-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'LTBM-BLACK-%';
