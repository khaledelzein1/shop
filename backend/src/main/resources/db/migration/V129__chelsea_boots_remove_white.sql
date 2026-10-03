-- Supprime la couleur Blanc de CHELSEA BOOTS (restent Noir et Marron).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'BTC-WHITE');

DELETE FROM product_variants
WHERE sku = 'BTC-WHITE';
