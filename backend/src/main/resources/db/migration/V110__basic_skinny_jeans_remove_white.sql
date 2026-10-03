-- BASIC SKINNY JEANS ne garde que Bleu foncé et Bleu : supprime la
-- couleur Blanc.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGC-WHITE');

DELETE FROM product_variants
WHERE sku = 'RGC-WHITE';
