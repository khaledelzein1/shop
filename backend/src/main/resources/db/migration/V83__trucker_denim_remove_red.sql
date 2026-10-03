-- TRUCKER DENIM JACKET ne garde que Noir et Bleu : supprime la couleur
-- Rouge.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNC-RED-%');

DELETE FROM product_variants
WHERE sku LIKE 'DNC-RED-%';
