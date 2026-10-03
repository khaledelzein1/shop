-- La migration précédente s'est appliquée avant la correction de couleur
-- (Beige -> Off-White) demandée par l'utilisateur ; corrige ici.

UPDATE product_variants
SET sku = replace(sku, 'CLL-BEIGE', 'CLL-OFFWHITE'),
    attributes = jsonb_set(attributes, '{color}', '"Off-White"')
WHERE sku = 'CLL-BEIGE';
