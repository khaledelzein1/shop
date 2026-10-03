-- Renomme la couleur Rouge de STRAIGHT-LEG JEANS en Bleu foncé et
-- remplace la photo générique par les 3 vraies photos produit fournies
-- (modèle, face, dos).

UPDATE product_variants
SET sku = replace(sku, 'LSCG-RED', 'LSCG-DARKBLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Dark Blue"')
WHERE sku = 'LSCG-RED';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'LSCG-DARKBLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/straight-leg-jeans-darkblue-model.png', 0),
    ('/products/straight-leg-jeans-darkblue-front.png', 1),
    ('/products/straight-leg-jeans-darkblue-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'LSCG-DARKBLUE';
