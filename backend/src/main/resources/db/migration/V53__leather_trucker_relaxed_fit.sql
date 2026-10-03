-- Renomme "Leather Trucker Jacket" en "RELAXED FIT LEATHER JACKET", fixe
-- le prix à 179 et remplace la photo générique de la couleur Noir par les
-- 2 vraies photos produit fournies (face, dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"RELAXED FIT LEATHER JACKET"'),
    price = 179
WHERE sku LIKE 'LTT-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTT-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-relaxed-front.png', 0),
    ('/products/jackets/leather-relaxed-back.png', 1)
) AS img(url, position)
WHERE pv.sku LIKE 'LTT-BLACK-%';
