-- Renomme la couleur Blanc de LEATHER-EFFECT JACKET en Vert et remplace
-- la photo générique par les 3 vraies photos produit fournies (modèle,
-- face, dos).

UPDATE product_variants
SET sku = replace(sku, 'LTBL-WHITE-', 'LTBL-GREEN-'),
    attributes = jsonb_set(attributes, '{color}', '"Green"')
WHERE sku LIKE 'LTBL-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBL-GREEN-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-effect-green-model.png', 0),
    ('/products/jackets/leather-effect-green-front.png', 1),
    ('/products/jackets/leather-effect-green-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'LTBL-GREEN-%';
