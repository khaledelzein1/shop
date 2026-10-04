-- Ajoute une dimension taille (XS/S/M/L/XL/XXL) à tous les modèles de
-- t-shirts, comme V23 pour les jackets. Contrairement à V23, la variante
-- existante (modèle + couleur) n'est pas supprimée : elle devient la taille
-- M (SKU suffixé "-M"), pour garder les paniers qui la contiennent. Les 5
-- autres tailles sont créées avec le même prix/stock et les mêmes photos.

CREATE TEMP TABLE tshirt_size_map AS
SELECT pv.id AS old_variant_id, pv.sku AS old_sku, pv.price, pv.attributes, pv.product_id, s.code AS size_code
FROM product_variants pv
CROSS JOIN (VALUES ('XS'), ('S'), ('L'), ('XL'), ('XXL')) AS s(code)
WHERE pv.product_id = (SELECT id FROM products WHERE slug = 'tshirts')
  AND pv.attributes ? 'model'
  AND NOT pv.attributes ? 'size';

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT old_sku || '-' || size_code, price, 20, true,
       attributes || jsonb_build_object('size', size_code), product_id, now(), now()
FROM tshirt_size_map;

-- Copie les photos de la variante d'origine sur chacune de ses 5 nouvelles tailles.
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, nv.product_id, nv.id, now(), now()
FROM tshirt_size_map tsm
JOIN product_variants nv ON nv.sku = tsm.old_sku || '-' || tsm.size_code
JOIN product_images pi ON pi.variant_id = tsm.old_variant_id;

-- La variante d'origine devient la taille M.
UPDATE product_variants
SET sku = sku || '-M',
    attributes = attributes || jsonb_build_object('size', 'M')
WHERE id IN (SELECT DISTINCT old_variant_id FROM tshirt_size_map);

DROP TABLE tshirt_size_map;
