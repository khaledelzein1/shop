-- Ajoute la photo portée par le modèle en 1re position de la galerie
-- Vert de Long Puffer Coat, sans retirer les photos existantes (juste
-- décalées en 2e, 3e et 4e position).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFL-GREEN-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/long-puffer-green-model.png', 0),
    ('/products/jackets/long-puffer-green-front.png', 1),
    ('/products/jackets/long-puffer-green-back.png', 2),
    ('/products/jackets/long-puffer-green-detail.png', 3)
) AS img(url, position)
WHERE pv.sku LIKE 'PFL-GREEN-%';
