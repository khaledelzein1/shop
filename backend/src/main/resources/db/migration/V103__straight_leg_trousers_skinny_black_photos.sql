-- Remplace la photo générique de la couleur Noir de "Straight Leg
-- Trousers" (champ Pants, section SKINNY) par les 3 vraies photos
-- produit fournies (modèle, face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGST-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/straight-leg-trousers-black-model.png', 0),
    ('/products/straight-leg-trousers-black-front.png', 1),
    ('/products/straight-leg-trousers-black-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'RGST-BLACK';
