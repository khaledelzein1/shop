-- Correction de V152 : la photo beige n'est pas une nouvelle couleur mais
-- une photo supplémentaire du T-shirt Essential Off-White existant. On la
-- rattache à la variante Off-White comme première photo (position 0, avant
-- sa photo existante qui reste en place) et on supprime la variante Beige
-- créée par erreur. Aucune photo n'est supprimée.

UPDATE product_images
SET variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-OFFWHITE')
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-BEIGE');

DELETE FROM product_variants WHERE sku = 'ESS-BEIGE';
