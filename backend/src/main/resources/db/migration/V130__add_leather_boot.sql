-- Ajoute un 4e modèle à la section BOOTS : "LEATHER BOOT", une seule
-- couleur (Noir), avec les 3 vraies photos produit fournies (modèle,
-- côté, face).

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'LB-BLACK', 99.95, 20, true,
       jsonb_build_object('fit', 'BOOTS', 'model', 'LEATHER BOOT', 'color', 'Black'),
       id, now(), now()
FROM products WHERE slug = 'shoes';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/leather-boot-model.png', 0),
    ('/products/leather-boot-side.png', 1),
    ('/products/leather-boot-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'LB-BLACK';
