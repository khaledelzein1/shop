-- BASIC SKINNY JEANS : renomme Rouge en Bleu foncé (nouvelles photos) et
-- Noir en Bleu (ses photos montraient déjà un jean bleu, l'étiquette de
-- couleur était juste incorrecte).

UPDATE product_variants
SET sku = replace(sku, 'RGC-RED', 'RGC-DARKBLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Dark Blue"')
WHERE sku = 'RGC-RED';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku = 'RGC-DARKBLUE');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/basic-skinny-jeans-darkblue-model.png', 0),
    ('/products/basic-skinny-jeans-darkblue-front.png', 1),
    ('/products/basic-skinny-jeans-darkblue-back.png', 2)
) AS img(url, position)
WHERE pv.sku = 'RGC-DARKBLUE';

UPDATE product_variants
SET sku = replace(sku, 'RGC-BLACK', 'RGC-BLUE'),
    attributes = jsonb_set(attributes, '{color}', '"Blue"')
WHERE sku = 'RGC-BLACK';
