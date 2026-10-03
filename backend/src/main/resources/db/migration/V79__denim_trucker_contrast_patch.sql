-- Renomme "Denim Trucker Jacket" (1er modèle Denim) en "CONTRAST PATCH
-- DENIM JACKET" et fixe le prix à 89.95.

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"CONTRAST PATCH DENIM JACKET"'),
    price = 89.95
WHERE sku LIKE 'DNT-%';
