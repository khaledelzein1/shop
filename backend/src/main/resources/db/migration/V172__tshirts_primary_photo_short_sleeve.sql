-- Catégorie T-shirts : la photo principale était encore le placeholder
-- picsum (photo de nature). On la remplace par une vraie photo produit
-- manches courtes (BASIC HEAVYWEIGHT T-SHIRT blanc, porté).

UPDATE product_images
SET url = '/products/basic-heavyweight-tshirt-white-model.png',
    updated_at = now()
WHERE is_primary = true
  AND url = 'https://picsum.photos/seed/tshirt-classic/600/600'
  AND product_id = (SELECT id FROM products WHERE slug = 'tshirts');
