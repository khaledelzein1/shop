-- Remplace la photo générique des 6 variantes Noir de "Hooded Puffer
-- Jacket" par les 2 vraies photos produit fournies (face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFH-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/hooded-puffer-black-front.png', 0),
    ('/products/jackets/hooded-puffer-black-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'PFH-BLACK-%';
