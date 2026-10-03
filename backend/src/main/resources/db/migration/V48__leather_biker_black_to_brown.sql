-- Le cuir de la veste est en réalité marron foncé, pas noir : corrige la
-- couleur affichée par la pastille de couleur.

UPDATE product_variants
SET sku = replace(sku, 'LTB-BLACK-', 'LTB-BROWN-'),
    attributes = jsonb_set(attributes, '{color}', '"Brown"')
WHERE sku LIKE 'LTB-BLACK-%';
