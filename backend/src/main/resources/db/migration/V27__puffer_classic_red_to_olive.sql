UPDATE product_variants
SET sku = replace(sku, 'PFC-RED-', 'PFC-OLIVE-'),
    attributes = jsonb_set(attributes, '{color}', '"Olive"')
WHERE sku LIKE 'PFC-RED-%';
