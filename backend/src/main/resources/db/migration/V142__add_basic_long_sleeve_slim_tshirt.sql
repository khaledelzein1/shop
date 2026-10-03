-- Ajoute un 4e modèle à la section Long Sleeve : "BASIC LONG-SLEEVE SLIM
-- T-SHIRT", une seule couleur (Noir), avec les 3 vraies photos produit
-- fournies (modèle, face, dos).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'MBLS-BLACK', 17.95, 20, true,
       jsonb_build_object('fit', 'Long Sleeve', 'model', 'BASIC LONG-SLEEVE SLIM T-SHIRT', 'color', 'Black'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-long-sleeve-slim-tshirt-model.png', 0),
    ('/products/basic-long-sleeve-slim-tshirt-front.png', 1),
    ('/products/basic-long-sleeve-slim-tshirt-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MBLS-BLACK';
