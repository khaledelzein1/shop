-- Renomme les 3 sections du champ Shoes : Sneakers -> TRAINERS, Sandals
-- -> LOAFERS, Boots -> BOOTS (3 modèles chacune, inchangé).

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"TRAINERS"')
FROM products p
WHERE pv.product_id = p.id AND p.slug = 'shoes' AND pv.attributes->>'fit' = 'Sneakers';

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"LOAFERS"')
FROM products p
WHERE pv.product_id = p.id AND p.slug = 'shoes' AND pv.attributes->>'fit' = 'Sandals';

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"BOOTS"')
FROM products p
WHERE pv.product_id = p.id AND p.slug = 'shoes' AND pv.attributes->>'fit' = 'Boots';
