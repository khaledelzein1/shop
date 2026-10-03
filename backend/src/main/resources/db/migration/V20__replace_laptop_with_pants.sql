-- Remplace le champ "Laptop" par un champ "Pants" : la catégorie
-- "Computers" devient "Pants", et le produit "Laptop Pro 15" devient le
-- produit conteneur "Pants Classic" (même pattern que "T-shirt Classic" :
-- 10 modèles de pantalon, chacun avec 3 couleurs Black/White/Red).

UPDATE categories SET name = 'Pants', slug = 'pants',
  description = 'Men''s and women''s pants and trousers'
WHERE slug = 'computers';

UPDATE products SET name = 'Pants Classic', slug = 'pants-classic',
  description = 'Classic fit pants, versatile everyday wear.', brand = 'WearCo'
WHERE slug = 'laptop-pro-15';

-- Les anciennes déclinaisons/images du laptop n'ont plus de sens pour un pantalon.
DELETE FROM product_images
WHERE product_id = (SELECT id FROM products WHERE slug = 'pants-classic');

DELETE FROM product_variants
WHERE product_id = (SELECT id FROM products WHERE slug = 'pants-classic');

-- 10 modèles de pantalon, 3 couleurs chacun.
INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-BLACK', v.price, 20, true, jsonb_build_object('model', v.model, 'color', 'Black'), p.id, now(), now()
FROM (VALUES
    ('Slim Fit Pants', 'SLIMP', 39.99),
    ('Straight Leg Pants', 'STRTP', 34.99),
    ('Cargo Pants', 'CARGP', 44.99),
    ('Chino Pants', 'CHINP', 42.99),
    ('Jogger Pants', 'JOGGP', 32.99),
    ('Wide Leg Pants', 'WIDEP', 37.99),
    ('Relaxed Fit Pants', 'RELXP', 36.99),
    ('Corduroy Pants', 'CORDP', 47.99),
    ('Linen Pants', 'LINP', 41.99),
    ('Track Pants', 'TRAKP', 29.99)
) AS v(model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'pants-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-WHITE', v.price, 20, true, jsonb_build_object('model', v.model, 'color', 'White'), p.id, now(), now()
FROM (VALUES
    ('Slim Fit Pants', 'SLIMP', 39.99),
    ('Straight Leg Pants', 'STRTP', 34.99),
    ('Cargo Pants', 'CARGP', 44.99),
    ('Chino Pants', 'CHINP', 42.99),
    ('Jogger Pants', 'JOGGP', 32.99),
    ('Wide Leg Pants', 'WIDEP', 37.99),
    ('Relaxed Fit Pants', 'RELXP', 36.99),
    ('Corduroy Pants', 'CORDP', 47.99),
    ('Linen Pants', 'LINP', 41.99),
    ('Track Pants', 'TRAKP', 29.99)
) AS v(model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'pants-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-RED', v.price, 20, true, jsonb_build_object('model', v.model, 'color', 'Red'), p.id, now(), now()
FROM (VALUES
    ('Slim Fit Pants', 'SLIMP', 39.99),
    ('Straight Leg Pants', 'STRTP', 34.99),
    ('Cargo Pants', 'CARGP', 44.99),
    ('Chino Pants', 'CHINP', 42.99),
    ('Jogger Pants', 'JOGGP', 32.99),
    ('Wide Leg Pants', 'WIDEP', 37.99),
    ('Relaxed Fit Pants', 'RELXP', 36.99),
    ('Corduroy Pants', 'CORDP', 47.99),
    ('Linen Pants', 'LINP', 41.99),
    ('Track Pants', 'TRAKP', 29.99)
) AS v(model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'pants-classic';

-- Photos par modèle/couleur. Black a une photo distincte par modèle ; White
-- et Red réutilisent 2 photos génériques vérifiées (comme pour les t-shirts).
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT CASE pv.sku
    WHEN 'SLIMP-BLACK' THEN 'https://images.unsplash.com/photo-1622450180332-3da1126f10a4?w=600&h=600&fit=crop'
    WHEN 'STRTP-BLACK' THEN 'https://images.unsplash.com/photo-1624320662882-b96a6bf0b3e4?w=600&h=600&fit=crop'
    WHEN 'CARGP-BLACK' THEN 'https://images.unsplash.com/photo-1661784396787-2a43e31a5689?w=600&h=600&fit=crop'
    WHEN 'TRAKP-BLACK' THEN 'https://images.unsplash.com/photo-1715532098035-a343b26eaeaa?w=600&h=600&fit=crop'
    ELSE 'https://images.unsplash.com/photo-1780566758193-cd5c36e2373f?w=600&h=600&fit=crop'
  END,
  0, (pv.sku = 'SLIMP-BLACK'), pv.product_id, pv.id, now(), now()
FROM product_variants pv WHERE pv.sku LIKE '%-BLACK' AND pv.sku LIKE '%P-%';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1636572326840-5ef4cb95897b?w=600&h=600&fit=crop', 1, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv WHERE pv.sku LIKE '%-WHITE' AND pv.sku LIKE '%P-%';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1786369910687-2a5da56b6e16?w=600&h=600&fit=crop', 2, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv WHERE pv.sku LIKE '%-RED' AND pv.sku LIKE '%P-%';
