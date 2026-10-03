-- Renomme la couleur Blanc de "Straight Leg Trousers" (champ Pants,
-- section SKINNY) en Bleu et remplace la photo générique par les 3
-- vraies photos produit fournies (modèle, face, dos).

UPDATE product_variants
SET sku = replace(sku, 'RGST-WHITE', 'RGST-BLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku = 'RGST-WHITE';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGST-BLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/straight-leg-trousers-blue-model.png', 0),
    ('/products/straight-leg-trousers-blue-front.png', 1),
    ('/products/straight-leg-trousers-blue-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'RGST-BLUE';

-- "Straight Leg Trousers" (SKINNY) ne garde que Noir et Bleu : supprime
-- la couleur Rouge.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGST-RED');

DELETE FROM product_variants
WHERE sku = 'RGST-RED';
