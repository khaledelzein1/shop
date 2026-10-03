UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"PUFFER GILET"')
WHERE sku LIKE 'PFB-%';
