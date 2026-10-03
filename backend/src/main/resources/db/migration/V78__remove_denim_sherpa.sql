-- Retire "Denim Jacket with Sherpa" du catalogue (le champ Denim ne doit
-- garder que 4 modèles).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNS-%');

DELETE FROM product_variants
WHERE sku LIKE 'DNS-%';
