-- Renomme la couleur Olive de LIGHTWEIGHT WATER-REPELLENT PADDED JACKET
-- en Marron et ajoute la photo portée par le modèle en 1re position de
-- la galerie, sans retirer les photos existantes.

UPDATE product_variants
SET sku = replace(sku, 'PFC-OLIVE-', 'PFC-BROWN-'),
    attributes = jsonb_set(attributes, '{color}', '"Brown"')
WHERE sku LIKE 'PFC-OLIVE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFC-BROWN-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/lightweight-padded-brown-model.png', 0),
    ('/products/jackets/lightweight-padded-red-front.png', 1),
    ('/products/jackets/lightweight-padded-red-back.png', 2),
    ('/products/jackets/lightweight-padded-red-detail.png', 3)
) AS img(url, position)
WHERE pv.sku LIKE 'PFC-BROWN-%';
