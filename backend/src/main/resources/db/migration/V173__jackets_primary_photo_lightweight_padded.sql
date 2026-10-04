-- Catégorie Jackets : aucune image n'était marquée principale, la carte
-- affichait donc la première image venue (HOODED PUFFER noir). On marque
-- la photo portée de la LIGHTWEIGHT WATER-REPELLENT PADDED JACKET noire
-- comme photo principale (une seule ligne, l'URL étant dupliquée par taille).

UPDATE product_images
SET is_primary = false, updated_at = now()
WHERE is_primary = true
  AND product_id = (SELECT id FROM products WHERE slug = 'jackets');

UPDATE product_images
SET is_primary = true, updated_at = now()
WHERE id = (
    SELECT min(id) FROM product_images
    WHERE url = '/products/jackets/lightweight-padded-black-model.png'
      AND product_id = (SELECT id FROM products WHERE slug = 'jackets'));
