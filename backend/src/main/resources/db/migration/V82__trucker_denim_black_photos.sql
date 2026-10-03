-- Remplace la photo générique de la couleur Noir de TRUCKER DENIM
-- JACKET par les 3 vraies photos produit fournies (modèle, face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNC-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/trucker-denim-black-model.png', 0),
    ('/products/jackets/trucker-denim-black-front.png', 1),
    ('/products/jackets/trucker-denim-black-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'DNC-BLACK-%';
