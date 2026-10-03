-- Inverse les 2e et 3e photos de la galerie de WOOL SUIT TROUSERS.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'SLJ-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/wool-suit-trousers-model.png', 0),
    ('/products/wool-suit-trousers-front.png', 1),
    ('/products/wool-suit-trousers-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'SLJ-BLACK-%';
