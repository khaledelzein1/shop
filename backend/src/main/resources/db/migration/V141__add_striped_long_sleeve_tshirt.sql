-- Ajoute un 3e modèle à la section Long Sleeve : "STRIPPED LONG SLEEVE
-- T-SHIRT", une seule couleur (Rouge), avec les 3 vraies photos produit
-- fournies (modèle, face, dos).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'MSLS-RED', 29.95, 20, true,
       jsonb_build_object('fit', 'Long Sleeve', 'model', 'STRIPPED LONG SLEEVE T-SHIRT', 'color', 'Red'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/striped-long-sleeve-tshirt-model.png', 0),
    ('/products/striped-long-sleeve-tshirt-front.png', 1),
    ('/products/striped-long-sleeve-tshirt-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MSLS-RED';
