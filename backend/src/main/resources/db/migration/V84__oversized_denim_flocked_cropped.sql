-- "Oversized Denim Jacket" (3e modèle Denim) ne doit exister qu'en une
-- seule couleur (Noir) : supprime les couleurs Rouge et Blanc, renomme
-- le modèle en "FLOCKED DENIM CROPPED JACKET", fixe le prix à 69.95 et
-- remplace la photo générique par les 3 vraies photos produit fournies
-- (modèle, face, dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNO-RED-%' OR sku LIKE 'DNO-WHITE-%');

DELETE FROM product_variants
WHERE sku LIKE 'DNO-RED-%' OR sku LIKE 'DNO-WHITE-%';

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"FLOCKED DENIM CROPPED JACKET"'),
    price = 69.95
WHERE sku LIKE 'DNO-BLACK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'DNO-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/flocked-denim-cropped-model.png', 0),
    ('/products/jackets/flocked-denim-cropped-front.png', 1),
    ('/products/jackets/flocked-denim-cropped-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'DNO-BLACK-%';
