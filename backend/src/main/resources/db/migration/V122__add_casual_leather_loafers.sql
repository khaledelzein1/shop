-- Ajoute un 4e modèle à la section LOAFERS : "CASUAL LEATHER LOAFERS",
-- une seule couleur (Beige), avec les 3 vraies photos produit fournies
-- (modèle, côté, face).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'CLL-BEIGE', 59.95, 20, true,
       jsonb_build_object('fit', 'LOAFERS', 'model', 'CASUAL LEATHER LOAFERS', 'color', 'Beige'),
       id, now(), now()
FROM products WHERE slug = 'shoes';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/casual-leather-loafers-model.png', 0),
    ('/products/casual-leather-loafers-side.png', 1),
    ('/products/casual-leather-loafers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'CLL-BEIGE';
