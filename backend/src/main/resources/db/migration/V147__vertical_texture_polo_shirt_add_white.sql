-- Ajoute une 3e couleur (White) au modèle "VERTICAL TEXTURE POLO SHIRT"
-- avec les 3 vraies photos produit fournies (modèle, face, dos).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'MVTP-WHITE', 25.95, 20, true,
       jsonb_build_object('fit', 'Polos', 'model', 'VERTICAL TEXTURE POLO SHIRT', 'color', 'White'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/vertical-texture-polo-shirt-white-model.png', 0),
    ('/products/vertical-texture-polo-shirt-white-front.png', 1),
    ('/products/vertical-texture-polo-shirt-white-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MVTP-WHITE';
