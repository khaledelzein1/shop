-- Ajoute un 4e rayon au catalogue : "Shoes", avec 3 sections (Sneakers /
-- Boots / Sandals), 3 modèles chacune, 3 couleurs (Black/White/Red) par
-- modèle — même pattern que Jackets/Pants/T-shirts.

INSERT INTO categories (name, slug, description, created_at, updated_at)
VALUES ('Shoes', 'shoes', 'Sneakers, boots and everyday shoes', now(), now());

INSERT INTO products (name, slug, description, brand, active, category_id, created_at, updated_at)
SELECT 'Shoes', 'shoes', 'Footwear for everyday wear.', 'WearCo', true, c.id, now(), now()
FROM categories c WHERE c.slug = 'shoes';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-BLACK', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'Black'), p.id, now(), now()
FROM (VALUES
    ('Sneakers', 'Classic Sneakers', 'SNK', 59.99),
    ('Sneakers', 'Running Sneakers', 'SNKR', 74.99),
    ('Sneakers', 'High-Top Sneakers', 'SNKH', 79.99),
    ('Boots', 'Chelsea Boots', 'BTC', 119.99),
    ('Boots', 'Combat Boots', 'BTCB', 129.99),
    ('Boots', 'Desert Boots', 'BTD', 99.99),
    ('Sandals', 'Slide Sandals', 'SDS', 29.99),
    ('Sandals', 'Strap Sandals', 'SDST', 34.99),
    ('Sandals', 'Sport Sandals', 'SDSP', 39.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'shoes';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-WHITE', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'White'), p.id, now(), now()
FROM (VALUES
    ('Sneakers', 'Classic Sneakers', 'SNK', 59.99),
    ('Sneakers', 'Running Sneakers', 'SNKR', 74.99),
    ('Sneakers', 'High-Top Sneakers', 'SNKH', 79.99),
    ('Boots', 'Chelsea Boots', 'BTC', 119.99),
    ('Boots', 'Combat Boots', 'BTCB', 129.99),
    ('Boots', 'Desert Boots', 'BTD', 99.99),
    ('Sandals', 'Slide Sandals', 'SDS', 29.99),
    ('Sandals', 'Strap Sandals', 'SDST', 34.99),
    ('Sandals', 'Sport Sandals', 'SDSP', 39.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'shoes';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-RED', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'Red'), p.id, now(), now()
FROM (VALUES
    ('Sneakers', 'Classic Sneakers', 'SNK', 59.99),
    ('Sneakers', 'Running Sneakers', 'SNKR', 74.99),
    ('Sneakers', 'High-Top Sneakers', 'SNKH', 79.99),
    ('Boots', 'Chelsea Boots', 'BTC', 119.99),
    ('Boots', 'Combat Boots', 'BTCB', 129.99),
    ('Boots', 'Desert Boots', 'BTD', 99.99),
    ('Sandals', 'Slide Sandals', 'SDS', 29.99),
    ('Sandals', 'Strap Sandals', 'SDST', 34.99),
    ('Sandals', 'Sport Sandals', 'SDSP', 39.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'shoes';

-- Photos : une photo générique par section (Sneakers/Boots/Sandals),
-- partagée par ses 3 modèles pour la couleur Black ; White/Red réutilisent
-- ces mêmes photos en attendant de vraies photos produit.
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT
  CASE pv.attributes ->> 'fit'
    WHEN 'Sneakers' THEN 'https://images.unsplash.com/photo-1676379827610-c380c52db0c6?w=600&h=600&fit=crop'
    WHEN 'Boots' THEN 'https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600&h=600&fit=crop'
    WHEN 'Sandals' THEN 'https://images.unsplash.com/photo-1603487742131-4160ec999306?w=600&h=600&fit=crop'
  END,
  0, (pv.sku = 'SNK-BLACK'), pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-BLACK' AND pv.product_id = (SELECT id FROM products WHERE slug = 'shoes');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT
  CASE pv.attributes ->> 'fit'
    WHEN 'Sneakers' THEN 'https://images.unsplash.com/photo-1676379827610-c380c52db0c6?w=600&h=600&fit=crop'
    WHEN 'Boots' THEN 'https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600&h=600&fit=crop'
    WHEN 'Sandals' THEN 'https://images.unsplash.com/photo-1603487742131-4160ec999306?w=600&h=600&fit=crop'
  END,
  0, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-WHITE' AND pv.product_id = (SELECT id FROM products WHERE slug = 'shoes');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT
  CASE pv.attributes ->> 'fit'
    WHEN 'Sneakers' THEN 'https://images.unsplash.com/photo-1676379827610-c380c52db0c6?w=600&h=600&fit=crop'
    WHEN 'Boots' THEN 'https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600&h=600&fit=crop'
    WHEN 'Sandals' THEN 'https://images.unsplash.com/photo-1603487742131-4160ec999306?w=600&h=600&fit=crop'
  END,
  0, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-RED' AND pv.product_id = (SELECT id FROM products WHERE slug = 'shoes');
