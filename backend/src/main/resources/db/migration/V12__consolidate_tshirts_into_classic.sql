-- Les 10 "t-shirts" ajoutés en V11 étaient des produits séparés. On les
-- fusionne comme déclinaisons du produit existant "T-shirt Classic" : chaque
-- variante gagne un attribut "modele" (repris du nom du produit d'origine)
-- pour permettre un sélecteur Modèle -> Couleur sur une seule fiche produit.

-- 1) Étiqueter les variantes avec leur modèle d'origine, tant que le lien
--    vers le produit source existe encore.
UPDATE product_variants pv
SET attributes = pv.attributes || jsonb_build_object('modele', p.name)
FROM products p
WHERE pv.product_id = p.id
  AND p.slug IN (
    'tshirt-essentiel', 'tshirt-premium-coton-bio', 'tshirt-oversize', 'tshirt-col-v',
    'tshirt-manches-longues', 'tshirt-sport-respirant', 'tshirt-vintage', 'tshirt-slim-fit',
    'tshirt-bio-recycle', 'tshirt-graphique-minimaliste'
  );

-- 2) Les images ne doivent plus être marquées "principales" une fois
--    rattachées à T-shirt Classic (qui a déjà sa propre image principale) ;
--    seule la couleur associée à la variante compte désormais.
UPDATE product_images pi
SET is_primary = false
FROM products p
WHERE pi.product_id = p.id
  AND p.slug IN (
    'tshirt-essentiel', 'tshirt-premium-coton-bio', 'tshirt-oversize', 'tshirt-col-v',
    'tshirt-manches-longues', 'tshirt-sport-respirant', 'tshirt-vintage', 'tshirt-slim-fit',
    'tshirt-bio-recycle', 'tshirt-graphique-minimaliste'
  );

-- 3) Rattacher variantes et images au produit "T-shirt Classic".
UPDATE product_variants pv
SET product_id = (SELECT id FROM products WHERE slug = 'tshirt-classic')
FROM products p
WHERE pv.product_id = p.id
  AND p.slug IN (
    'tshirt-essentiel', 'tshirt-premium-coton-bio', 'tshirt-oversize', 'tshirt-col-v',
    'tshirt-manches-longues', 'tshirt-sport-respirant', 'tshirt-vintage', 'tshirt-slim-fit',
    'tshirt-bio-recycle', 'tshirt-graphique-minimaliste'
  );

UPDATE product_images pi
SET product_id = (SELECT id FROM products WHERE slug = 'tshirt-classic')
FROM products p
WHERE pi.product_id = p.id
  AND p.slug IN (
    'tshirt-essentiel', 'tshirt-premium-coton-bio', 'tshirt-oversize', 'tshirt-col-v',
    'tshirt-manches-longues', 'tshirt-sport-respirant', 'tshirt-vintage', 'tshirt-slim-fit',
    'tshirt-bio-recycle', 'tshirt-graphique-minimaliste'
  );

-- 4) Les 10 produits temporaires sont maintenant vides, on les supprime.
DELETE FROM products
WHERE slug IN (
  'tshirt-essentiel', 'tshirt-premium-coton-bio', 'tshirt-oversize', 'tshirt-col-v',
  'tshirt-manches-longues', 'tshirt-sport-respirant', 'tshirt-vintage', 'tshirt-slim-fit',
  'tshirt-bio-recycle', 'tshirt-graphique-minimaliste'
);
