-- Remplace la photo générique de la couleur Rouge de LEATHER-EFFECT
-- JACKET par les 3 vraies photos produit fournies (modèle, face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTBL-RED-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-effect-red-model.png', 0),
    ('/products/jackets/leather-effect-red-front.png', 1),
    ('/products/jackets/leather-effect-red-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'LTBL-RED-%';
