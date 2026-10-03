-- Renomme la section "Slim" du champ Pants en "Trousers".

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"Trousers"')
FROM products p
WHERE pv.product_id = p.id
  AND p.slug = 'pants'
  AND pv.attributes->>'fit' = 'Slim';
