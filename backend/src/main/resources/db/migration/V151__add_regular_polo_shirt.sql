-- Ajoute un 3e modèle à la section Polos : "REGULAR POLO SHIRT", une
-- seule couleur (Green), avec les 3 vraies photos produit fournies
-- (modèle, face, dos). Prix aligné sur les autres polos (25.95).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'MRPS-GREEN', 25.95, 20, true,
       jsonb_build_object('fit', 'Polos', 'model', 'REGULAR POLO SHIRT', 'color', 'Green'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/regular-polo-shirt-model.png', 0),
    ('/products/regular-polo-shirt-front.png', 1),
    ('/products/regular-polo-shirt-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MRPS-GREEN';
