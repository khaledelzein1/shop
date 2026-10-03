UPDATE product_variants
SET sku = 'PCB-BROWN', attributes = jsonb_set(attributes, '{color}', '"Brown"')
WHERE sku = 'PCB-OFFWHITE';

UPDATE product_images SET url = '/products/oversized-cotton-brown.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-BROWN');
