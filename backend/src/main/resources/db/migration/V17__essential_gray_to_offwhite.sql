UPDATE product_variants
SET sku = 'ESS-OFFWHITE', attributes = jsonb_set(attributes, '{color}', '"Off-White"')
WHERE sku = 'ESS-GRIS';
