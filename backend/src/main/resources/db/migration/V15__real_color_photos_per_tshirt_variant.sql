-- Chaque variante couleur d'un modèle avait la même photo que ses 2 autres
-- couleurs (seule la sélection visuelle changeait, pas la photo), ce qui
-- donnait l'impression que les swatches Noir/Rouge ne "marchaient pas" sur
-- des modèles dont la photo de référence n'était ni noire ni rouge (ex.
-- "T-shirt Essential" n'avait qu'une photo blanche). On assigne désormais une
-- vraie photo par couple (modèle, couleur).

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1610502778270-c5c6f4c7d575?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Essential' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1624373607006-348f61ea2d76?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Essential' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1610502778270-c5c6f4c7d575?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Premium Organic Cotton' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1778671394516-8270eac13c42?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Premium Organic Cotton' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1624373607006-348f61ea2d76?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Premium Organic Cotton' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1618453292459-53424b66bb6a?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Oversize' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1661181475147-bbd20ef65781?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Oversize' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1624373607006-348f61ea2d76?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Oversize' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1603364374348-890320e28dda?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt V-Neck' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1775498652326-3193b3feda46?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt V-Neck' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1621072156002-e2fccdc0b176?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Long Sleeve' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1608976989382-d913f97920c8?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Long Sleeve' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1778587614557-ca0167ba7a04?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Breathable Sport' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1559166631-ef208440c75a?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Breathable Sport' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1781863074773-836134430654?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Breathable Sport' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1553434102-e307729966ee?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Vintage' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1597802540570-8de28950ae33?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Vintage' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1610502778270-c5c6f4c7d575?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Recycled Organic' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1778671394516-8270eac13c42?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Recycled Organic' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1624373607006-348f61ea2d76?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Recycled Organic' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1775817104298-522393e1d72b?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Minimalist Graphic' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1553434102-e307729966ee?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Minimalist Graphic' AND attributes ->> 'color' = 'White');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1597802540570-8de28950ae33?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Minimalist Graphic' AND attributes ->> 'color' = 'Red');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1610502778270-c5c6f4c7d575?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Slim Fit' AND attributes ->> 'color' = 'Black');
UPDATE product_images SET url = 'https://images.unsplash.com/photo-1624373607006-348f61ea2d76?w=600&h=600&fit=crop'
WHERE variant_id = (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Slim Fit' AND attributes ->> 'color' = 'Red');
