-- RELAXED FIT LEATHER JACKET ne doit exister qu'en une seule couleur
-- (Noir) : supprime les couleurs Rouge et Blanc.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTT-RED-%' OR sku LIKE 'LTT-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'LTT-RED-%' OR sku LIKE 'LTT-WHITE-%';
