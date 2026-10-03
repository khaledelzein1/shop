-- Ajoute une 2e couleur (Blue) au modèle "RETRO-STYLE TRAINERS"
-- avec les 3 vraies photos produit fournies (modèle, côté, face).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'SNK-BLUE', 20.95, 20, true,
       jsonb_build_object('fit', 'TRAINERS', 'model', 'RETRO-STYLE TRAINERS', 'color', 'Blue'),
       id, now(), now()
FROM products WHERE slug = 'shoes';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/retro-style-trainers-blue-model.png', 0),
    ('/products/retro-style-trainers-blue-side.png', 1),
    ('/products/retro-style-trainers-blue-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SNK-BLUE';
