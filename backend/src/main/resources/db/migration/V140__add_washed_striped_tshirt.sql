-- Ajoute un 2e modèle à la section Long Sleeve : "WASHED STRIPPED
-- T-SHIRT", une seule couleur (Bleu), avec les 3 vraies photos produit
-- fournies (modèle, face, dos).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'MWST-BLUE', 35.95, 20, true,
       jsonb_build_object('fit', 'Long Sleeve', 'model', 'WASHED STRIPPED T-SHIRT', 'color', 'Blue'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/washed-striped-tshirt-model.png', 0),
    ('/products/washed-striped-tshirt-front.png', 1),
    ('/products/washed-striped-tshirt-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'MWST-BLUE';
