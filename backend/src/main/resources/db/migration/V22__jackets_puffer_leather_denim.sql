-- Transforme le produit de test "Produit Test Playwright" (0 variante, 0
-- image, non référencé par du code ou des tests) en "Jackets Classic" :
-- 3 sections par type (Puffer / Leather / Denim), 5 modèles chacune, 3
-- couleurs (Black/White/Red) par modèle — même pattern que Pants Classic.

UPDATE categories SET name = 'Jackets', slug = 'jackets',
  description = 'Puffer, leather and denim jackets'
WHERE slug = 'accessoires-test';

UPDATE products SET name = 'Jackets Classic', slug = 'jackets-classic',
  description = 'Classic jackets for every season.', brand = 'WearCo'
WHERE slug = 'produit-test-playwright';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-BLACK', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'Black'), p.id, now(), now()
FROM (VALUES
    ('Puffer', 'Puffer Jacket Classic', 'PFC', 89.99),
    ('Puffer', 'Puffer Vest', 'PFV', 64.99),
    ('Puffer', 'Hooded Puffer Jacket', 'PFH', 94.99),
    ('Puffer', 'Puffer Bomber Jacket', 'PFB', 99.99),
    ('Puffer', 'Long Puffer Coat', 'PFL', 119.99),
    ('Leather', 'Leather Biker Jacket', 'LTB', 149.99),
    ('Leather', 'Leather Bomber Jacket', 'LTBM', 139.99),
    ('Leather', 'Leather Trucker Jacket', 'LTT', 129.99),
    ('Leather', 'Leather Blazer', 'LTBL', 159.99),
    ('Leather', 'Leather Moto Jacket', 'LTM', 144.99),
    ('Denim', 'Denim Trucker Jacket', 'DNT', 69.99),
    ('Denim', 'Denim Jacket Classic', 'DNC', 64.99),
    ('Denim', 'Oversized Denim Jacket', 'DNO', 74.99),
    ('Denim', 'Denim Jacket with Sherpa', 'DNS', 84.99),
    ('Denim', 'Cropped Denim Jacket', 'DNCR', 72.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'jackets-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-WHITE', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'White'), p.id, now(), now()
FROM (VALUES
    ('Puffer', 'Puffer Jacket Classic', 'PFC', 89.99),
    ('Puffer', 'Puffer Vest', 'PFV', 64.99),
    ('Puffer', 'Hooded Puffer Jacket', 'PFH', 94.99),
    ('Puffer', 'Puffer Bomber Jacket', 'PFB', 99.99),
    ('Puffer', 'Long Puffer Coat', 'PFL', 119.99),
    ('Leather', 'Leather Biker Jacket', 'LTB', 149.99),
    ('Leather', 'Leather Bomber Jacket', 'LTBM', 139.99),
    ('Leather', 'Leather Trucker Jacket', 'LTT', 129.99),
    ('Leather', 'Leather Blazer', 'LTBL', 159.99),
    ('Leather', 'Leather Moto Jacket', 'LTM', 144.99),
    ('Denim', 'Denim Trucker Jacket', 'DNT', 69.99),
    ('Denim', 'Denim Jacket Classic', 'DNC', 64.99),
    ('Denim', 'Oversized Denim Jacket', 'DNO', 74.99),
    ('Denim', 'Denim Jacket with Sherpa', 'DNS', 84.99),
    ('Denim', 'Cropped Denim Jacket', 'DNCR', 72.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'jackets-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-RED', v.price, 20, true,
       jsonb_build_object('fit', v.fit, 'model', v.model, 'color', 'Red'), p.id, now(), now()
FROM (VALUES
    ('Puffer', 'Puffer Jacket Classic', 'PFC', 89.99),
    ('Puffer', 'Puffer Vest', 'PFV', 64.99),
    ('Puffer', 'Hooded Puffer Jacket', 'PFH', 94.99),
    ('Puffer', 'Puffer Bomber Jacket', 'PFB', 99.99),
    ('Puffer', 'Long Puffer Coat', 'PFL', 119.99),
    ('Leather', 'Leather Biker Jacket', 'LTB', 149.99),
    ('Leather', 'Leather Bomber Jacket', 'LTBM', 139.99),
    ('Leather', 'Leather Trucker Jacket', 'LTT', 129.99),
    ('Leather', 'Leather Blazer', 'LTBL', 159.99),
    ('Leather', 'Leather Moto Jacket', 'LTM', 144.99),
    ('Denim', 'Denim Trucker Jacket', 'DNT', 69.99),
    ('Denim', 'Denim Jacket Classic', 'DNC', 64.99),
    ('Denim', 'Oversized Denim Jacket', 'DNO', 74.99),
    ('Denim', 'Denim Jacket with Sherpa', 'DNS', 84.99),
    ('Denim', 'Cropped Denim Jacket', 'DNCR', 72.99)
) AS v(fit, model, sku_prefix, price)
CROSS JOIN products p
WHERE p.slug = 'jackets-classic';

-- Photos : chaque type (Puffer/Leather/Denim) a sa propre photo distincte
-- pour Black (partagée par ses 5 modèles) ; White/Red réutilisent 2 photos
-- génériques vérifiées.
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT
  CASE pv.attributes ->> 'fit'
    WHEN 'Puffer' THEN 'https://images.unsplash.com/photo-1614031679232-0dae776a72ee?w=600&h=600&fit=crop'
    WHEN 'Leather' THEN 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600&h=600&fit=crop'
    WHEN 'Denim' THEN 'https://images.unsplash.com/photo-1611312449408-fcece27cdbb7?w=600&h=600&fit=crop'
  END,
  0, (pv.sku = 'PFC-BLACK'), pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-BLACK' AND pv.product_id = (SELECT id FROM products WHERE slug = 'jackets-classic');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1784822041225-c60458304d65?w=600&h=600&fit=crop', 1, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-WHITE' AND pv.product_id = (SELECT id FROM products WHERE slug = 'jackets-classic');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1742151103389-dfb048fca775?w=600&h=600&fit=crop', 2, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-RED' AND pv.product_id = (SELECT id FROM products WHERE slug = 'jackets-classic');
