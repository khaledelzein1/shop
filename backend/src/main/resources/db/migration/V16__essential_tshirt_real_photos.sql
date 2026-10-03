-- Remplace les photos stock de "T-shirt Essential" par les vraies photos
-- produit fournies (Noir/Blanc + une 3e couleur "Gray" à la place de Rouge),
-- servies statiquement par le front sous /products/.

UPDATE product_variants
SET sku = 'ESS-GRIS', attributes = jsonb_set(attributes, '{color}', '"Gray"')
WHERE sku = 'ESS-ROUGE';

UPDATE product_images SET url = '/products/tshirt-essential-black.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-NOIR');

UPDATE product_images SET url = '/products/tshirt-essential-white.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-BLANC');

UPDATE product_images SET url = '/products/tshirt-essential-gray.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'ESS-GRIS');
