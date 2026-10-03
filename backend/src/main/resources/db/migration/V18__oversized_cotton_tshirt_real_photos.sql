-- Renomme "T-shirt Premium Organic Cotton" en "Oversized Fit Cotton T-shirt"
-- et remplace ses photos stock par les vraies photos produit fournies
-- (Bleu à la place de Noir, Blanc, Off-White à la place de Rouge).

UPDATE product_variants
SET sku = 'PCB-BLUE',
    attributes = jsonb_set(attributes, '{color}', '"Blue"') || jsonb_build_object('model', 'Oversized Fit Cotton T-shirt')
WHERE sku = 'PCB-NOIR';

UPDATE product_variants
SET attributes = jsonb_build_object('color', 'White', 'model', 'Oversized Fit Cotton T-shirt')
WHERE sku = 'PCB-BLANC';

UPDATE product_variants
SET sku = 'PCB-OFFWHITE',
    attributes = jsonb_set(attributes, '{color}', '"Off-White"') || jsonb_build_object('model', 'Oversized Fit Cotton T-shirt')
WHERE sku = 'PCB-ROUGE';

UPDATE product_images SET url = '/products/oversized-cotton-blue.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-BLUE');

UPDATE product_images SET url = '/products/oversized-cotton-white.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-BLANC');

UPDATE product_images SET url = '/products/oversized-cotton-offwhite.png'
WHERE variant_id = (SELECT id FROM product_variants WHERE sku = 'PCB-OFFWHITE');
