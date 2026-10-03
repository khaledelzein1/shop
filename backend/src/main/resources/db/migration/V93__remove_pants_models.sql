-- Retire "Slim Fit Chino", "Slim Fit Linen Pants" et "Slim Cargo Pants"
-- du catalogue.

DELETE FROM product_images
WHERE variant_id IN (
    SELECT id FROM product_variants
    WHERE sku LIKE 'SLC-%' OR sku LIKE 'SLLN-%' OR sku LIKE 'SLCG-%'
);

DELETE FROM product_variants
WHERE sku LIKE 'SLC-%' OR sku LIKE 'SLLN-%' OR sku LIKE 'SLCG-%';
