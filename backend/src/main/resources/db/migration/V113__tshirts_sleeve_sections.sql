-- Ajoute le champ "fit" au catalogue T-shirts pour créer les sections
-- Long Sleeve / Short Sleeve / Polos. "T-shirt Long Sleeve" va dans Long
-- Sleeve, tous les autres modèles (dont Oversized Fit Cotton T-shirt et
-- T-shirt Essential) vont dans Short Sleeve. Polos reste vide pour
-- l'instant (aucun polo au catalogue).

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"Long Sleeve"')
FROM products p
WHERE pv.product_id = p.id
  AND p.slug = 'tshirts'
  AND pv.attributes->>'model' = 'T-shirt Long Sleeve';

UPDATE product_variants pv
SET attributes = jsonb_set(attributes, '{fit}', '"Short Sleeve"')
FROM products p
WHERE pv.product_id = p.id
  AND p.slug = 'tshirts'
  AND pv.attributes->>'model' IS NOT NULL
  AND pv.attributes->>'model' <> 'T-shirt Long Sleeve';
