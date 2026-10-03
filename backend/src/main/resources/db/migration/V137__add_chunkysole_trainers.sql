-- Ajoute un 4e modèle à la section TRAINERS : "CHUNKYSOLE TRAINERS", une
-- seule couleur (Noir), avec les 3 vraies photos produit fournies
-- (modèle, côté, face).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'SNKC-BLACK', 39.95, 20, true,
       jsonb_build_object('fit', 'TRAINERS', 'model', 'CHUNKYSOLE TRAINERS', 'color', 'Black'),
       id, now(), now()
FROM products WHERE slug = 'shoes';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/chunkysole-trainers-model.png', 0),
    ('/products/chunkysole-trainers-side.png', 1),
    ('/products/chunkysole-trainers-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SNKC-BLACK';
