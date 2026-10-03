-- Restaure la photo à plat (retirée par erreur lors du remplacement de la
-- photo de face) comme 2e photo de la galerie, après la photo portée par
-- le modèle et avant la photo de dos.

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku LIKE 'LTT-BLACK-%');

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/jackets/leather-relaxed-front.png', 0),
    ('/products/jackets/leather-relaxed-flat-front.png', 1),
    ('/products/jackets/leather-relaxed-back.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'LTT-BLACK-%';
