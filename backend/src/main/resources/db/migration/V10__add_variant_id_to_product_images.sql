-- Permet de lier une image à une déclinaison précise (ex. photo différente par
-- couleur), en plus de l'image générique du produit. Nullable : une image peut
-- rester rattachée au produit seul (galerie générale, pas de déclinaison).

ALTER TABLE product_images
    ADD COLUMN variant_id BIGINT REFERENCES product_variants (id) ON DELETE CASCADE;

CREATE INDEX idx_product_images_variant_id ON product_images (variant_id);
