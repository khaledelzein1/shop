-- Retire "T-shirt Slim Fit", "T-shirt Vintage" et "T-shirt Breathable
-- Sport" de la section Short Sleeve.

DELETE FROM product_images
WHERE variant_id IN (
    SELECT id FROM product_variants
    WHERE sku LIKE 'SPRT-%' OR sku LIKE 'SLIM-%' OR sku LIKE 'VNTG-%'
);

DELETE FROM product_variants
WHERE sku LIKE 'SPRT-%' OR sku LIKE 'SLIM-%' OR sku LIKE 'VNTG-%';
