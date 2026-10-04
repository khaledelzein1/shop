-- Ajoute une 4e couleur (Beige) au modèle "T-shirt Essential", affichée en
-- premier : sa photo prend la position 0 et les photos existantes (Black,
-- White, Off-White) sont décalées d'un cran, sans en supprimer aucune.

UPDATE product_images
SET position = position + 1
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('ESS-NOIR', 'ESS-BLANC', 'ESS-OFFWHITE'));

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'ESS-BEIGE', 14.99, 20, true,
       jsonb_build_object('fit', 'Short Sleeve', 'model', 'T-shirt Essential', 'color', 'Beige'),
       id, now(), now()
FROM products WHERE slug = 'tshirts';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT '/products/tshirt-essential-beige.png', 0, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku = 'ESS-BEIGE';
