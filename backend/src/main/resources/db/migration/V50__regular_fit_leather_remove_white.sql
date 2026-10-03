-- REGULAR FIT LEATHER JACKET ne doit avoir que 2 couleurs (Rouge, Noir) :
-- supprime la 3e couleur (Blanc).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBM-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'LTBM-WHITE-%';
