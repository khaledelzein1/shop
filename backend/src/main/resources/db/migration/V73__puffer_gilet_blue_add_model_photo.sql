-- Ajoute la photo portée par le modèle en 1re position de la galerie
-- Bleu de PUFFER GILET, sans retirer les photos existantes (juste
-- décalées en 2e et 3e position).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFB-BLUE-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/puffer-bomber-blue-model.png', 0),
    ('/products/jackets/puffer-bomber-blue-front.png', 1),
    ('/products/jackets/puffer-bomber-blue-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFB-BLUE-%';
