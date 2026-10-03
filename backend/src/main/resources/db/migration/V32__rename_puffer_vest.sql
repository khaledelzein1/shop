UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"REPPELENT JACKET"')
WHERE sku LIKE 'PFV-%';
