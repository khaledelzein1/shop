-- Ajoute une 2e couleur (White) au modèle "NEW RETRO STYLE TRAINERS"
-- avec les 3 vraies photos produit fournies (modèle, côté, face).
-- La couleur Rouge a déjà été supprimée par V133.

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'SNKH-WHITE', 29.95, 20, true,
       jsonb_build_object('fit', 'TRAINERS', 'model', 'NEW RETRO STYLE TRAINERS', 'color', 'White'),
       id, now(), now()
FROM products WHERE slug = 'shoes';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/new-retro-style-trainers-white-model.png', 0),
    ('/products/new-retro-style-trainers-white-side.png', 1),
    ('/products/new-retro-style-trainers-white-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SNKH-WHITE';
