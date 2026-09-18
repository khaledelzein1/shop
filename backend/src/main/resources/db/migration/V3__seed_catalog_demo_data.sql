-- Données de démo pour pouvoir tester/présenter le catalogue immédiatement
-- (utile en dev et pour une démo recruteur). Purement illustratif : à ne
-- pas rejouer telle quelle sur un environnement de prod réel.

INSERT INTO categories (name, slug, description, created_at, updated_at) VALUES
    ('Informatique', 'informatique', 'Ordinateurs, composants et accessoires informatiques', now(), now()),
    ('Vêtements', 'vetements', 'Vêtements pour homme et femme', now(), now());

INSERT INTO products (name, slug, description, brand, active, category_id, created_at, updated_at)
SELECT 'Laptop Pro 15', 'laptop-pro-15',
       'Ordinateur portable 15 pouces, idéal pour le développement et la bureautique.',
       'TechBrand', true, c.id, now(), now()
FROM categories c WHERE c.slug = 'informatique';

INSERT INTO products (name, slug, description, brand, active, category_id, created_at, updated_at)
SELECT 'T-shirt Classic', 'tshirt-classic',
       'T-shirt en coton bio, coupe classique.',
       'WearCo', true, c.id, now(), now()
FROM categories c WHERE c.slug = 'vetements';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'LP15-8-256', 899.99, 15, true, '{"ram": "8 Go", "stockage": "256 Go SSD"}'::jsonb, p.id, now(), now()
FROM products p WHERE p.slug = 'laptop-pro-15';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'LP15-16-512', 1199.99, 8, true, '{"ram": "16 Go", "stockage": "512 Go SSD"}'::jsonb, p.id, now(), now()
FROM products p WHERE p.slug = 'laptop-pro-15';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'TS-S-NOIR', 19.99, 50, true, '{"taille": "S", "couleur": "Noir"}'::jsonb, p.id, now(), now()
FROM products p WHERE p.slug = 'tshirt-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'TS-M-NOIR', 19.99, 40, true, '{"taille": "M", "couleur": "Noir"}'::jsonb, p.id, now(), now()
FROM products p WHERE p.slug = 'tshirt-classic';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT 'TS-L-BLANC', 19.99, 0, true, '{"taille": "L", "couleur": "Blanc"}'::jsonb, p.id, now(), now()
FROM products p WHERE p.slug = 'tshirt-classic';

INSERT INTO product_images (url, position, is_primary, product_id, created_at, updated_at)
SELECT 'https://picsum.photos/seed/laptop-pro-15/600/600', 0, true, p.id, now(), now()
FROM products p WHERE p.slug = 'laptop-pro-15';

INSERT INTO product_images (url, position, is_primary, product_id, created_at, updated_at)
SELECT 'https://picsum.photos/seed/tshirt-classic/600/600', 0, true, p.id, now(), now()
FROM products p WHERE p.slug = 'tshirt-classic';
