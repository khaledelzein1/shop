-- "Slim Track Pants" ne doit exister qu'en une seule couleur (Noir) :
-- supprime les couleurs Rouge et Blanc, renomme le modèle en "RELAXED
-- FIT CARGO TROUSERS", fixe le prix à 59.95 et remplace la photo
-- générique par les 3 vraies photos produit fournies (modèle, face,
-- dos).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SLTK-RED', 'SLTK-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SLTK-RED', 'SLTK-WHITE');

UPDATE product_variants
SET attributes = jsonb_set(attributes, '{model}', '"RELAXED FIT CARGO TROUSERS"'),
    price = 59.95
WHERE sku = 'SLTK-BLACK';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'SLTK-BLACK');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/relaxed-cargo-trousers-model.png', 0),
    ('/products/relaxed-cargo-trousers-front.png', 1),
    ('/products/relaxed-cargo-trousers-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'SLTK-BLACK';

-- PLATED RELAXED FIT CHINO TROUSERS ne doit garder que sa couleur Noire.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SLCD-RED', 'SLCD-WHITE'));

DELETE FROM product_variants
WHERE sku IN ('SLCD-RED', 'SLCD-WHITE');
