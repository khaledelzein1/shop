-- "BASIC SLIM FIT T-SHIRT" Black : remplace son ancienne photo par les 3
-- vraies photos produit fournies (modèle, face, dos). Positions 1 à 3 pour
-- garder l'ordre des couleurs (Off-White, Black, White).

DELETE FROM product_images
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-NOIR');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-slim-fit-tshirt-black-model.png', 1),
    ('/products/basic-slim-fit-tshirt-black-front.png', 2),
    ('/products/basic-slim-fit-tshirt-black-back.png', 3)
) AS img(url, position)
WHERE pv.sku = 'ESS-NOIR';
