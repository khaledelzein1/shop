-- Retire "Regular Fit Corduroy" et "Regular Cargo Pants" du champ Pants,
-- section SKINNY.

DELETE FROM product_images
WHERE variant_id IN (
    SELECT id FROM product_variants
    WHERE sku LIKE 'RGCG-%' OR sku LIKE 'RGCD-%'
);

DELETE FROM product_variants
WHERE sku LIKE 'RGCG-%' OR sku LIKE 'RGCD-%';
