-- Renomme les 6 variantes Rouge de "Hooded Puffer Jacket" en Bleu et
-- remplace leur photo générique par les 2 vraies photos produit fournies.

UPDATE product_variants
SET sku = replace(sku, 'PFH-RED-', 'PFH-BLUE-'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku LIKE 'PFH-RED-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFH-BLUE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/hooded-puffer-blue-front.png', 0),
    ('/products/jackets/hooded-puffer-blue-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'PFH-BLUE-%';
