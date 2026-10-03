-- Ajoute un 5e modèle à la section Long Sleeve : "COTTON LINEN T-SHIRT",
-- une seule couleur (Blanc), avec les 3 vraies photos produit fournies
-- (modèle, face, dos).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'MCLT-WHITE', 35.95, 20, true,
       jsonb_build_object('fit', 'Long Sleeve', 'model', 'COTTON LINEN T-SHIRT', 'color', 'White'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/cotton-linen-tshirt-model.png', 0),
    ('/products/cotton-linen-tshirt-front.png', 1),
    ('/products/cotton-linen-tshirt-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MCLT-WHITE';
