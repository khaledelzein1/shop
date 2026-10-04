-- "RINGER BASIC CONTRAST RIBBED T-SHIRT" : ajoute une couleur Blue (17.95)
-- avec les 3 vraies photos produit fournies (modèle, face, dos), placées
-- après toutes les autres photos pour venir après la couleur White.

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'OVS-BLUE', 17.95, 20, true,
       jsonb_build_object('fit', 'Short Sleeve', 'model', 'RINGER BASIC CONTRAST RIBBED T-SHIRT', 'color', 'Blue'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/ringer-contrast-ribbed-tshirt-blue-model.png', 12),
    ('/products/ringer-contrast-ribbed-tshirt-blue-front.png', 13),
    ('/products/ringer-contrast-ribbed-tshirt-blue-back.png', 14)
) AS img(url, position)
WHERE pv.sku = 'OVS-BLUE';
