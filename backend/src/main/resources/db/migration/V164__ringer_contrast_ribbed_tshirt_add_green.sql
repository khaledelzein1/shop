-- "RINGER BASIC CONTRAST RIBBED T-SHIRT" : ajoute une couleur Green (17.95)
-- avec les 3 vraies photos produit fournies (modèle, face, dos), placées
-- après toutes les autres photos pour venir après les couleurs White et Blue.

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'OVS-GREEN', 17.95, 20, true,
       jsonb_build_object('fit', 'Short Sleeve', 'model', 'RINGER BASIC CONTRAST RIBBED T-SHIRT', 'color', 'Green'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/ringer-contrast-ribbed-tshirt-green-model.png', 15),
    ('/products/ringer-contrast-ribbed-tshirt-green-front.png', 16),
    ('/products/ringer-contrast-ribbed-tshirt-green-back.png', 17)
) AS img(url, position)
WHERE pv.sku = 'OVS-GREEN';
