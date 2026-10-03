-- Chaque modèle de t-shirt avait la même photo générique (noire/blanche/rouge)
-- réutilisée pour les 10 modèles, ce qui les rendait indiscernables. On donne
-- à chaque modèle sa propre photo représentative (coupe/style différents),
-- réutilisée sur ses 3 variantes couleur (le swatch change la sélection, pas
-- la photo, pour ces modèles).

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1651761179569-4ba2aa054997?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Essential');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1759572095384-1a7e646d0d4f?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Premium Organic Cotton');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1622519407650-3df9883f76a5?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Oversize');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1620799139507-2a76f79a2f4d?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt V-Neck');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1614495039368-525273956716?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Long Sleeve');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1728718248311-2fdb76913d94?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Breathable Sport');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1580518439473-a4935d24adb9?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Vintage');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1523381294911-8d3cead13475?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Recycled Organic');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1758214872888-41b1ab469e00?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Minimalist Graphic');

UPDATE product_images SET url = 'https://images.unsplash.com/photo-1592994238317-fcf75c5466fd?w=600&h=600&fit=crop'
WHERE variant_id IN (SELECT id FROM product_variants WHERE attributes ->> 'model' = 'T-shirt Slim Fit');
