-- "BASIC HEAVYWEIGHT T-SHIRT" : la couleur Blue devient Green, avec les 3
-- vraies photos produit fournies (modèle, face, dos) à la place de son
-- ancienne photo ; la 1re garde la position 0 pour rester la 1re couleur.
-- La couleur Brown est supprimée.

UPDATE product_variants
SET sku = 'PCB-GREEN',
    attributes = jsonb_set(attributes, '{color}', '"Green"')
WHERE sku = 'PCB-BLUE';

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-GREEN');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-heavyweight-tshirt-green-model.png', 0),
    ('/products/basic-heavyweight-tshirt-green-front.png', 8),
    ('/products/basic-heavyweight-tshirt-green-back.png', 9)
) AS img(url, position)
WHERE pv.sku = 'PCB-GREEN';

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-BROWN');

DELETE FROM product_variants WHERE sku = 'PCB-BROWN';
