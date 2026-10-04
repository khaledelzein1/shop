-- Renomme le modèle "Long Puffer Coat" en majuscules, comme les autres
-- modèles : "LONG PUFFER COAT".

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LONG PUFFER COAT"'),
    updated_at = now()
WHERE attributes ->> 'model' = 'Long Puffer Coat';
