-- Renomme les 3 produits conteneurs pour un nom + une URL plus courts et
-- cohérents avec le nom de leur catégorie.

UPDATE products SET name = 'Jackets', slug = 'jackets' WHERE slug = 'jackets-classic';
UPDATE products SET name = 'Pants', slug = 'pants' WHERE slug = 'pants-classic';
UPDATE products SET name = 'T-shirts', slug = 'tshirts' WHERE slug = 'tshirt-classic';
