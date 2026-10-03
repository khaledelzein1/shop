-- CONTRAST PATCH DENIM JACKET ne doit exister qu'en une seule couleur
-- (Noir) : supprime les couleurs Rouge et Blanc, et remplace la photo
-- générique par les 3 vraies photos produit fournies (modèle, face,
-- dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNT-RED-%' OR sku LIKE 'DNT-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'DNT-RED-%' OR sku LIKE 'DNT-WHITE-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNT-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/contrast-patch-denim-model.png', 0),
    ('/products/jackets/contrast-patch-denim-front.png', 1),
    ('/products/jackets/contrast-patch-denim-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'DNT-BLACK-%';
