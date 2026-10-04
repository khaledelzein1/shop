-- Renomme le modèle "Leather Moto Jacket" en majuscules, comme les autres
-- modèles : "LEATHER MOTO JACKET".

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LEATHER MOTO JACKET"'),
    updated_at = now()
WHERE attributes ->> 'model' = 'Leather Moto Jacket';
