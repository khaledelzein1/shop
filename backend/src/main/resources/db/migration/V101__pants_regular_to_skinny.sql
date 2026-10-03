-- Renomme la section "Regular" du champ Pants en "SKINNY".

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"SKINNY"')
FROM products p
WHERE pv.product_id = p.id
  AND p.slug = 'pants'
  AND pv.attributes->>'fit' = 'Regular';
