-- Le front est passé en anglais ; on traduit aussi les données de démo
-- affichées telles quelles (catégories, descriptions, attributs de variante)
-- pour rester cohérent. Les SKU restent inchangés (ce sont des codes, pas du
-- texte affiché traduit).

UPDATE categories SET name = 'Computers', slug = 'computers',
  description = 'Computers, components and computer accessories'
WHERE slug = 'informatique';

UPDATE categories SET name = 'Clothing', slug = 'clothing',
  description = 'Men''s and women''s clothing'
WHERE slug = 'vetements';

UPDATE products SET description = 'A 15-inch laptop, ideal for development and office work.'
WHERE slug = 'laptop-pro-15';

UPDATE products SET description = 'Organic cotton t-shirt, classic fit.'
WHERE slug = 'tshirt-classic';

-- couleur -> color (valeur traduite)
UPDATE product_variants
SET attributes = (attributes - 'couleur') || jsonb_build_object('color',
  CASE attributes ->> 'couleur'
    WHEN 'Noir' THEN 'Black'
    WHEN 'Blanc' THEN 'White'
    WHEN 'Rouge' THEN 'Red'
    WHEN 'Bleu' THEN 'Blue'
    ELSE attributes ->> 'couleur'
  END)
WHERE attributes ? 'couleur';

-- taille -> size (valeurs S/M/L inchangées)
UPDATE product_variants
SET attributes = (attributes - 'taille') || jsonb_build_object('size', attributes ->> 'taille')
WHERE attributes ? 'taille';

-- modele -> model (valeur traduite)
UPDATE product_variants
SET attributes = (attributes - 'modele') || jsonb_build_object('model',
  CASE attributes ->> 'modele'
    WHEN 'T-shirt Essentiel' THEN 'T-shirt Essential'
    WHEN 'T-shirt Premium Coton Bio' THEN 'T-shirt Premium Organic Cotton'
    WHEN 'T-shirt Oversize' THEN 'T-shirt Oversize'
    WHEN 'T-shirt Col V' THEN 'T-shirt V-Neck'
    WHEN 'T-shirt Manches Longues' THEN 'T-shirt Long Sleeve'
    WHEN 'T-shirt Sport Respirant' THEN 'T-shirt Breathable Sport'
    WHEN 'T-shirt Vintage' THEN 'T-shirt Vintage'
    WHEN 'T-shirt Slim Fit' THEN 'T-shirt Slim Fit'
    WHEN 'T-shirt Bio Recyclé' THEN 'T-shirt Recycled Organic'
    WHEN 'T-shirt Graphique Minimaliste' THEN 'T-shirt Minimalist Graphic'
    ELSE attributes ->> 'modele'
  END)
WHERE attributes ? 'modele';

-- stockage -> storage, "Go" -> "GB"
UPDATE product_variants
SET attributes = (attributes - 'stockage') || jsonb_build_object('storage', replace(attributes ->> 'stockage', 'Go', 'GB'))
WHERE attributes ? 'stockage';

-- ram : la clé reste "ram", "Go" -> "GB" dans la valeur
UPDATE product_variants
SET attributes = jsonb_set(attributes, '{ram}', to_jsonb(replace(attributes ->> 'ram', 'Go', 'GB')))
WHERE attributes ? 'ram';
