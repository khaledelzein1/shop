-- LEATHER PENNY LOAFERS ne garde que Marron et Bleu : supprime la
-- couleur Rouge.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SDS-RED');

DELETE FROM product_variants
WHERE sku = 'SDS-RED';
