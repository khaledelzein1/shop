-- Renomme la couleur Rouge de Regular Track Pants en Bleu clair et
-- remplace la photo générique par les 3 vraies photos produit fournies
-- (modèle, face, dos).

UPDATE product_variants
SET sku = replace(sku, 'RGTK-RED', 'RGTK-LIGHTBLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Light Blue"')
WHERE sku = 'RGTK-RED';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGTK-LIGHTBLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/regular-track-pants-lightblue-model.png', 0),
    ('/products/regular-track-pants-lightblue-front.png', 1),
    ('/products/regular-track-pants-lightblue-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'RGTK-LIGHTBLUE';
