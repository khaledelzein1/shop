-- Les couleurs Beige et Black de "VERTICAL TEXTURE POLO SHIRT" sont en
-- réalité un autre polo : on les sépare en un modèle distinct "REGULAR
-- FIT TEXTURED POLO SHIRT" (même section Polos, même prix). White, Brown
-- et Blue Gray restent "VERTICAL TEXTURE POLO SHIRT". Les variantes
-- gardent leur id (paniers et photos inchangés) ; seul le sku change de
-- préfixe pour refléter le nouveau modèle.

UPDATE product_variants
SET sku = replace(sku, 'MVTP-', 'MRTP-'),
    attributes = jsonb_set(attributes, '{model}', '"REGULAR FIT TEXTURED POLO SHIRT"')
WHERE sku IN ('MVTP-BEIGE', 'MVTP-BLACK');
