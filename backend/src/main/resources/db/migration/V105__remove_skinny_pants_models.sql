-- Retire "Classic Joggers" et "Regular Fit Jeans" du champ Pants,
-- section SKINNY.

DELETE FROM product_images
WHERE variant_id IN (
    SELECT id FROM product_variants
    WHERE sku LIKE 'RGJG-%' OR sku LIKE 'RGJ-%'
);

DELETE FROM product_variants
WHERE sku LIKE 'RGJG-%' OR sku LIKE 'RGJ-%';
