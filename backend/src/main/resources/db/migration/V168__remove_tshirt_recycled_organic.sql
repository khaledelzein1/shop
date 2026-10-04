-- Supprime le modèle "T-shirt Recycled Organic" (couleurs Black, White, Red)
-- et ses photos.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Recycled Organic');

DELETE FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Recycled Organic';
