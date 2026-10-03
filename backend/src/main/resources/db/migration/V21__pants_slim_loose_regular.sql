-- Réorganise "Pants Classic" en 3 sections par coupe (Slim / Loose /
-- Regular), 7 modèles chacune, chaque modèle avec 3 couleurs
-- (Black/White/Red) comme pour les t-shirts. Remplace le set plat de 10
-- modèles créé en V20.

DELETE FROM product_images
WHERE product_id = (SELECT id FROM products WHERE slug = 'pants-classic');

DELETE FROM product_variants
WHERE product_id = (SELECT id FROM products WHERE slug = 'pants-classic');

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-BLACK', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'Black'), p.id, now(), now()
FROM (VALUES
    ('Slim', 'Slim Fit Chino', 'SLC', 42.99),
    ('Slim', 'Slim Fit Jeans', 'SLJ', 44.99),
    ('Slim', 'Slim Fit Trousers', 'SLT', 39.99),
    ('Slim', 'Slim Cargo Pants', 'SLCG', 46.99),
    ('Slim', 'Slim Fit Corduroy', 'SLCD', 47.99),
    ('Slim', 'Slim Track Pants', 'SLTK', 31.99),
    ('Slim', 'Slim Fit Linen Pants', 'SLLN', 41.99),
    ('Loose', 'Loose Fit Cargo', 'LSCG', 46.99),
    ('Loose', 'Wide Leg Trousers', 'LSWD', 38.99),
    ('Loose', 'Loose Fit Jeans', 'LSJ', 43.99),
    ('Loose', 'Baggy Joggers', 'LSBG', 34.99),
    ('Loose', 'Relaxed Cargo Pants', 'LSRC', 45.99),
    ('Loose', 'Loose Fit Linen', 'LSLN', 40.99),
    ('Loose', 'Palazzo Pants', 'LSPZ', 39.99),
    ('Regular', 'Regular Fit Chino', 'RGC', 41.99),
    ('Regular', 'Straight Leg Trousers', 'RGST', 36.99),
    ('Regular', 'Regular Fit Jeans', 'RGJ', 42.99),
    ('Regular', 'Classic Joggers', 'RGJG', 32.99),
    ('Regular', 'Regular Cargo Pants', 'RGCG', 44.99),
    ('Regular', 'Regular Fit Corduroy', 'RGCD', 46.99),
    ('Regular', 'Regular Track Pants', 'RGTK', 29.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'pants-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-WHITE', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'White'), p.id, now(), now()
FROM (VALUES
    ('Slim', 'Slim Fit Chino', 'SLC', 42.99),
    ('Slim', 'Slim Fit Jeans', 'SLJ', 44.99),
    ('Slim', 'Slim Fit Trousers', 'SLT', 39.99),
    ('Slim', 'Slim Cargo Pants', 'SLCG', 46.99),
    ('Slim', 'Slim Fit Corduroy', 'SLCD', 47.99),
    ('Slim', 'Slim Track Pants', 'SLTK', 31.99),
    ('Slim', 'Slim Fit Linen Pants', 'SLLN', 41.99),
    ('Loose', 'Loose Fit Cargo', 'LSCG', 46.99),
    ('Loose', 'Wide Leg Trousers', 'LSWD', 38.99),
    ('Loose', 'Loose Fit Jeans', 'LSJ', 43.99),
    ('Loose', 'Baggy Joggers', 'LSBG', 34.99),
    ('Loose', 'Relaxed Cargo Pants', 'LSRC', 45.99),
    ('Loose', 'Loose Fit Linen', 'LSLN', 40.99),
    ('Loose', 'Palazzo Pants', 'LSPZ', 39.99),
    ('Regular', 'Regular Fit Chino', 'RGC', 41.99),
    ('Regular', 'Straight Leg Trousers', 'RGST', 36.99),
    ('Regular', 'Regular Fit Jeans', 'RGJ', 42.99),
    ('Regular', 'Classic Joggers', 'RGJG', 32.99),
    ('Regular', 'Regular Cargo Pants', 'RGCG', 44.99),
    ('Regular', 'Regular Fit Corduroy', 'RGCD', 46.99),
    ('Regular', 'Regular Track Pants', 'RGTK', 29.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'pants-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-RED', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'Red'), p.id, now(), now()
FROM (VALUES
    ('Slim', 'Slim Fit Chino', 'SLC', 42.99),
    ('Slim', 'Slim Fit Jeans', 'SLJ', 44.99),
    ('Slim', 'Slim Fit Trousers', 'SLT', 39.99),
    ('Slim', 'Slim Cargo Pants', 'SLCG', 46.99),
    ('Slim', 'Slim Fit Corduroy', 'SLCD', 47.99),
    ('Slim', 'Slim Track Pants', 'SLTK', 31.99),
    ('Slim', 'Slim Fit Linen Pants', 'SLLN', 41.99),
    ('Loose', 'Loose Fit Cargo', 'LSCG', 46.99),
    ('Loose', 'Wide Leg Trousers', 'LSWD', 38.99),
    ('Loose', 'Loose Fit Jeans', 'LSJ', 43.99),
    ('Loose', 'Baggy Joggers', 'LSBG', 34.99),
    ('Loose', 'Relaxed Cargo Pants', 'LSRC', 45.99),
    ('Loose', 'Loose Fit Linen', 'LSLN', 40.99),
    ('Loose', 'Palazzo Pants', 'LSPZ', 39.99),
    ('Regular', 'Regular Fit Chino', 'RGC', 41.99),
    ('Regular', 'Straight Leg Trousers', 'RGST', 36.99),
    ('Regular', 'Regular Fit Jeans', 'RGJ', 42.99),
    ('Regular', 'Classic Joggers', 'RGJG', 32.99),
    ('Regular', 'Regular Cargo Pants', 'RGCG', 44.99),
    ('Regular', 'Regular Fit Corduroy', 'RGCD', 46.99),
    ('Regular', 'Regular Track Pants', 'RGTK', 29.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'pants-classic';

-- Photos : quelques modèles ont une photo distincte, les autres réutilisent
-- 3 photos génériques noir/blanc/rouge vérifiées (même approche que V20).
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT CASE pv.sku
    WHEN 'SLT-BLACK' THEN 'https://images.unsplash.com/photo-1622450180332-3da1126f10a4?w=600&h=600&fit=crop'
    WHEN 'LSCG-BLACK' THEN 'https://images.unsplash.com/photo-1661784396787-2a43e31a5689?w=600&h=600&fit=crop'
    WHEN 'RGST-BLACK' THEN 'https://images.unsplash.com/photo-1624320662882-b96a6bf0b3e4?w=600&h=600&fit=crop'
    WHEN 'RGTK-BLACK' THEN 'https://images.unsplash.com/photo-1715532098035-a343b26eaeaa?w=600&h=600&fit=crop'
    ELSE 'https://images.unsplash.com/photo-1780566758193-cd5c36e2373f?w=600&h=600&fit=crop'
  END,
  0, (pv.sku = 'SLC-BLACK'), pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-BLACK' AND pv.product_id = (SELECT id FROM products WHERE slug = 'pants-classic');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1636572326840-5ef4cb95897b?w=600&h=600&fit=crop', 1, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-WHITE' AND pv.product_id = (SELECT id FROM products WHERE slug = 'pants-classic');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1786369910687-2a5da56b6e16?w=600&h=600&fit=crop', 2, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-RED' AND pv.product_id = (SELECT id FROM products WHERE slug = 'pants-classic');
