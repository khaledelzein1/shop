-- "FLORAL PRINT T-SHIRT" : ajoute une couleur Grey (35.95) avec les 3
-- vraies photos produit fournies (modèle, face, dos), placées après toutes
-- les autres photos pour venir après la couleur Mint.

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'COLV-GREY', 35.95, 20, true,
       jsonb_build_object('fit', 'Short Sleeve', 'model', 'FLORAL PRINT T-SHIRT', 'color', 'Grey'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/floral-print-tshirt-grey-model.png', 22),
    ('/products/floral-print-tshirt-grey-front.png', 23),
    ('/products/floral-print-tshirt-grey-back.png', 24)
) AS img(url, position)
WHERE pv.sku = 'COLV-GREY';
