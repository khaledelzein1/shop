-- Remplace l'image aléatoire (Picsum) du produit de démo "Laptop Pro 15"
-- par une vraie photo d'ordinateur portable.

UPDATE product_images
SET url = 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=600&fit=crop',
    updated_at = now()
FROM products p
WHERE product_images.product_id = p.id
  AND p.slug = 'laptop-pro-15'
  AND product_images.is_primary = true;
