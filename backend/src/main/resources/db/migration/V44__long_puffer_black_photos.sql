-- Remplace la photo générique des variantes Noir de "Long Puffer Coat" par
-- les 3 vraies photos produit fournies (face, dos, détail zip).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFL-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/long-puffer-black-front.png', 0),
    ('/products/jackets/long-puffer-black-back.png', 1),
    ('/products/jackets/long-puffer-black-detail.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFL-BLACK-%';
