-- Renomme les 6 variantes Blanc de "LIGHTWEIGHT WATER-REPELLENT PADDED
-- JACKET" en Bleu et remplace leur photo générique par les 3 vraies photos
-- produit fournies (face, dos, détail zip) — même pattern que Noir/Olive.

UPDATE product_variants
SET sku = replace(sku, 'PFC-WHITE-', 'PFC-BLUE-'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku LIKE 'PFC-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFC-BLUE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/lightweight-padded-blue-front.png', 0),
    ('/products/jackets/lightweight-padded-blue-back.png', 1),
    ('/products/jackets/lightweight-padded-blue-detail.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFC-BLUE-%';
