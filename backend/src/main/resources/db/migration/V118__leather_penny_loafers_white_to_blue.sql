-- Renomme la couleur Blanc de LEATHER PENNY LOAFERS en Bleu et remplace
-- la photo générique par les 3 vraies photos produit fournies (modèle,
-- côté, face).

UPDATE product_variants
SET sku = replace(sku, 'SDS-WHITE', 'SDS-BLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku = 'SDS-WHITE';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SDS-BLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/leather-penny-loafers-blue-model.png', 0),
    ('/products/leather-penny-loafers-blue-side.png', 1),
    ('/products/leather-penny-loafers-blue-front.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SDS-BLUE';
