UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"COMBINED QUILTED JACKET"'),
    price = 39.95
WHERE sku LIKE 'PFH-%';
