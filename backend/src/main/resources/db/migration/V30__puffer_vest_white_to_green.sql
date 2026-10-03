-- Renomme les 6 variantes Blanc de "Puffer Vest" en Vert et remplace leur
-- photo générique par les 2 vraies photos produit fournies (face, dos).

UPDATE product_variants
SET sku = replace(sku, 'PFV-WHITE-', 'PFV-GREEN-'),
    attributes = jsonb_set(attributes, '{color}', '"Green"')
WHERE sku LIKE 'PFV-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFV-GREEN-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/puffer-vest-green-front.png', 0),
    ('/products/jackets/puffer-vest-green-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'PFV-GREEN-%';
