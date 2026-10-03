-- Restructure "Slim Fit Jeans" into "WOOL SUIT TROUSERS": single color
-- (Noir), tailles XS-XXL, prix 119. Une ligne de panier pointant vers
-- l'ancienne variante sans taille (SLJ-BLACK) est migrée vers la
-- nouvelle variante M plutôt que d'être perdue.

WITH old_variant AS (
    SELECT id, product_id FROM product_variants WHERE sku = 'SLJ-BLACK'
), new_variants AS (
    INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
    SELECT
        'SLJ-BLACK-' || sizes.size,
        119,
        20,
        true,
        jsonb_build_object('fit', 'Trousers', 'color', 'Black', 'model', 'WOOL SUIT TROUSERS', 'size', sizes.size),
        old_variant.product_id,
        now(),
        now()
    FROM old_variant
    CROSS JOIN (VALUES ('XS'), ('S'), ('M'), ('L'), ('XL'), ('XXL')) AS sizes(size)
    RETURNING id, sku
)
UPDATE cart_items
SET variant_id = (SELECT id FROM new_variants WHERE sku = 'SLJ-BLACK-M')
WHERE variant_id = (SELECT id FROM old_variant);

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT img.url, img.position, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
CROSS JOIN (VALUES
    ('/products/wool-suit-trousers-model.png', 0),
    ('/products/wool-suit-trousers-back.png', 1),
    ('/products/wool-suit-trousers-front.png', 2)
) AS img(url, position)
WHERE pv.sku LIKE 'SLJ-BLACK-%';

DELETE FROM product_images
WHERE variant_id IN (SELECT id FROM product_variants WHERE sku IN ('SLJ-RED', 'SLJ-WHITE', 'SLJ-BLACK'));

DELETE FROM product_variants
WHERE sku IN ('SLJ-RED', 'SLJ-WHITE', 'SLJ-BLACK');
