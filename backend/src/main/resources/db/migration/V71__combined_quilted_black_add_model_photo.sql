-- Ajoute la photo portée par le modèle en 1re position de la galerie
-- Noir de COMBINED QUILTED JACKET, sans retirer les photos existantes
-- (juste décalées en 2e et 3e position).

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'PFH-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/hooded-puffer-black-model.png', 0),
    ('/products/jackets/hooded-puffer-black-front.png', 1),
    ('/products/jackets/hooded-puffer-black-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'PFH-BLACK-%';
