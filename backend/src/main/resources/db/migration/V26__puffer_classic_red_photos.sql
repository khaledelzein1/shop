-- Remplace la photo générique des 6 variantes Rouge de "LIGHTWEIGHT
-- WATER-REPELLENT PADDED JACKET" par les 3 vraies photos produit fournies
-- (face, dos, détail zip), comme pour le Noir en V24.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFC-RED-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/lightweight-padded-red-front.png', 0),
    ('/products/jackets/lightweight-padded-red-back.png', 1),
    ('/products/jackets/lightweight-padded-red-detail.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFC-RED-%';
