-- "BASIC SLIM FIT T-SHIRT" : la couleur Black est en réalité Navy Blue
-- (photos de V155). Renommage en place : la variante garde son id, donc
-- paniers et photos restent valides.

UPDATE product_variants
SET sku = 'ESS-NAVY',
    attributes = jsonb_set(attributes, '{color}', '"Navy Blue"')
WHERE sku = 'ESS-NOIR';
