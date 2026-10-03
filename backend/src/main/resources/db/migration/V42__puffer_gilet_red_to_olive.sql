-- Renomme les 6 variantes Rouge de "PUFFER GILET" en Vert olive et
-- remplace leur photo générique par les 2 vraies photos produit fournies.

UPDATE product_variants
SET sku = replace(sku, 'PFB-RED-', 'PFB-OLIVE-'),
    attributes = jsonb_set(attributes, '{color}', '"Olive"')
WHERE sku LIKE 'PFB-RED-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFB-OLIVE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/puffer-gilet-olive-front.png', 0),
    ('/products/jackets/puffer-gilet-olive-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'PFB-OLIVE-%';
