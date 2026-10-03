-- Ajoute la photo portée par le modèle en 1re position de la galerie de
-- CROPPED FIT LEATHER BIKER JACKET, sans retirer les photos existantes
-- (juste décalées en 2e et 3e position).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTB-BROWN-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-biker-model.png', 0),
    ('/products/jackets/leather-biker-front.png', 1),
    ('/products/jackets/leather-biker-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'LTB-BROWN-%';
