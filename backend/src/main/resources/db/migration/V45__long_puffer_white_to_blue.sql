-- Renomme les 6 variantes Blanc de "Long Puffer Coat" en Bleu et remplace
-- leur photo générique par les 3 vraies photos produit fournies (face,
-- dos, détail zip).

UPDATE product_variants
SET sku = replace(sku, 'PFL-WHITE-', 'PFL-BLUE-'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku LIKE 'PFL-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFL-BLUE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/long-puffer-blue-front.png', 0),
    ('/products/jackets/long-puffer-blue-back.png', 1),
    ('/products/jackets/long-puffer-blue-detail.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFL-BLUE-%';
