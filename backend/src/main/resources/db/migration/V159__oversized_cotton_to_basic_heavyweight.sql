-- Renomme le modèle "Oversized Fit Cotton T-shirt" en "BASIC HEAVYWEIGHT
-- T-SHIRT" et fixe son prix à 17.95 (toutes couleurs). Ajoute une couleur
-- Black avec les 3 vraies photos produit fournies (modèle, face, dos),
-- placées après les couleurs existantes (Blue, White, Brown).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"BASIC HEAVYWEIGHT T-SHIRT"'),
    price = 17.95
WHERE attributes ->> 'model' = 'Oversized Fit Cotton T-shirt';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'PCB-BLACK', 17.95, 20, true,
       jsonb_build_object('fit', 'Short Sleeve', 'model', 'BASIC HEAVYWEIGHT T-SHIRT', 'color', 'Black'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-heavyweight-tshirt-black-model.png', 3),
    ('/products/basic-heavyweight-tshirt-black-front.png', 4),
    ('/products/basic-heavyweight-tshirt-black-back.png', 5)
) AS img(url, position)
WHERE pv.sku = 'PCB-BLACK';
