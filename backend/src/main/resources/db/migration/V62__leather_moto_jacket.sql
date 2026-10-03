-- Leather Moto Jacket ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, fixe le prix à 45.99 et remplace
-- la photo générique par les 3 vraies photos produit fournies (modèle,
-- face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTM-RED-%' OR sku LIKE 'LTM-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'LTM-RED-%' OR sku LIKE 'LTM-WHITE-%';

UPDATE product_variants
SET price = 45.99
WHERE sku LIKE 'LTM-BLACK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTM-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-moto-model.png', 0),
    ('/products/jackets/leather-moto-front.png', 1),
    ('/products/jackets/leather-moto-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'LTM-BLACK-%';
