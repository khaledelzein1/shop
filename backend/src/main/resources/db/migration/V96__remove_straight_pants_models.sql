-- Retire "Loose Fit Jeans", "Baggy Joggers", "Relaxed Cargo Pants",
-- "Loose Fit Linen" et "Palazzo Pants" du champ Straight (ne garde que
-- "Loose Fit Cargo" et "Wide Leg Trousers").

DELETE FROM product_images
WHERE variant_id IN (
    SELECT id FROM product_variants
    WHERE sku LIKE 'LSBG-%' OR sku LIKE 'LSJ-%' OR sku LIKE 'LSLN-%' OR sku LIKE 'LSPZ-%' OR sku LIKE 'LSRC-%'
);

DELETE FROM product_variants
WHERE sku LIKE 'LSBG-%' OR sku LIKE 'LSJ-%' OR sku LIKE 'LSLN-%' OR sku LIKE 'LSPZ-%' OR sku LIKE 'LSRC-%';
