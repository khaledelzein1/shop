-- Renomme la section "Loose" du champ Pants en "Straight".

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"Straight"')
FROM products p
WHERE pv.product_id = p.id
  AND p.slug = 'pants'
  AND pv.attributes->>'fit' = 'Loose';
