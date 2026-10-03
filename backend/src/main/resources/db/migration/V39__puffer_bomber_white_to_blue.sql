-- Renomme les 6 variantes Blanc de "Puffer Bomber Jacket" en Bleu et
-- remplace leur photo générique par les 2 vraies photos produit fournies.

UPDATE product_variants
SET sku = replace(sku, 'PFB-WHITE-', 'PFB-BLUE-'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku LIKE 'PFB-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFB-BLUE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/puffer-bomber-blue-front.png', 0),
    ('/products/jackets/puffer-bomber-blue-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'PFB-BLUE-%';
