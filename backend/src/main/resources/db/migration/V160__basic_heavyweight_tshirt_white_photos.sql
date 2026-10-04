-- "BASIC HEAVYWEIGHT T-SHIRT" White : remplace son ancienne photo par les
-- 3 vraies photos produit fournies (modèle, face, dos). La 1re garde la
-- position 1 pour conserver l'ordre des couleurs (Blue, White, Brown,
-- Black) ; les 2 suivantes passent après toutes les autres photos.

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-BLANC');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-heavyweight-tshirt-white-model.png', 1),
    ('/products/basic-heavyweight-tshirt-white-front.png', 6),
    ('/products/basic-heavyweight-tshirt-white-back.png', 7)
) AS img(url, position)
WHERE pv.sku = 'PCB-BLANC';
