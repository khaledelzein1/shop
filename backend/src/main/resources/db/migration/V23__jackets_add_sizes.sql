-- Ajoute une dimension taille (XS/S/M/L/XL/XXL) aux jackets uniquement :
-- chaque variante (modèle + couleur) existante est éclatée en 6 variantes,
-- une par taille, avec le même prix/photo, un SKU suffixé et l'attribut
-- "size" en plus. T-shirts et pants ne sont pas concernés.

CREATE TEMP TABLE variant_size_map AS
SELECT pv.id AS old_variant_id, pv.sku AS old_sku, pv.price, pv.attributes, pv.product_id, s.code AS size_code
FROM product_variants pv
CROSS JOIN (VALUES ('XS'), ('S'), ('M'), ('L'), ('XL'), ('XXL')) AS s(code)
WHERE pv.product_id = (SELECT id FROM products WHERE slug = 'jackets-classic');

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT old_sku || '-' || size_code, price, 20, true,
       attributes || jsonb_build_object('size', size_code), product_id, now(), now()
FROM variant_size_map;

-- Copie l'image de la variante d'origine sur chacune de ses 6 nouvelles tailles.
-- is_primary reste à false partout : le fallback "premier par position" de
-- ProductSummaryResponse suffit à retrouver la bonne vignette catalogue.
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT pi.url, pi.position, false, nv.product_id, nv.id, now(), now()
FROM variant_size_map vsm
JOIN product_variants nv ON nv.sku = vsm.old_sku || '-' || vsm.size_code
JOIN product_images pi ON pi.variant_id = vsm.old_variant_id;

-- Supprime les anciennes variantes (sans taille) et leurs images.
DELETE FROM product_images WHERE variant_id IN (SELECT old_variant_id FROM variant_size_map);
DELETE FROM product_variants WHERE id IN (SELECT old_variant_id FROM variant_size_map);

DROP TABLE variant_size_map;
