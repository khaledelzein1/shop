-- Renomme "Leather Blazer" en "LEATHER-EFFECT JACKET", fixe le prix à
-- 39.99 et remplace la photo générique de la couleur Noir par les 3
-- vraies photos produit fournies (modèle, face, dos).

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"LEATHER-EFFECT JACKET"'),
    price = 39.99
WHERE sku LIKE 'LTBL-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBL-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-effect-model.png', 0),
    ('/products/jackets/leather-effect-front.png', 1),
    ('/products/jackets/leather-effect-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'LTBL-BLACK-%';
