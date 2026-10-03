-- Fixe le prix de Regular Track Pants à 35.99 pour toutes les couleurs
-- et remplace la photo générique de la couleur Noir par les 3 vraies
-- photos produit fournies (modèle, face, dos).

UPDATE product_variants
SET price = 35.99
WHERE sku LIKE 'RGTK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGTK-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/regular-track-pants-black-model.png', 0),
    ('/products/regular-track-pants-black-front.png', 1),
    ('/products/regular-track-pants-black-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'RGTK-BLACK';
