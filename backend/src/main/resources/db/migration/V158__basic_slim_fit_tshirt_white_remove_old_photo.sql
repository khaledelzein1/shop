-- "BASIC SLIM FIT T-SHIRT" White : supprime sa 4e photo (l'ancienne photo
-- à plat), ne gardant que les 3 vraies photos produit (modèle, face, dos).

DELETE FROM product_images
WHERE url = '/products/tshirt-essential-white.png'
  AND variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-BLANC');
