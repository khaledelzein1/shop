-- Ajoute 10 produits T-shirt supplémentaires au catalogue de démo, chacun avec
-- son propre prix et 3 déclinaisons couleur (Noir / Blanc / Rouge). Chaque
-- déclinaison a sa propre photo (V10 a ajouté variant_id à product_images),
-- pour que changer de couleur sur la fiche produit change aussi la photo.

INSERT INTO products (name, slug, description, brand, active, category_id, created_at, updated_at)
SELECT v.name, v.slug, v.description, 'WearCo', true, c.id, now(), now()
FROM (VALUES
    ('T-shirt Essentiel', 'tshirt-essentiel', 'T-shirt basique en coton, coupe droite, indispensable du quotidien.'),
    ('T-shirt Premium Coton Bio', 'tshirt-premium-coton-bio', 'T-shirt en coton biologique certifié, doux et respectueux de l''environnement.'),
    ('T-shirt Oversize', 'tshirt-oversize', 'Coupe oversize tendance, parfait pour un look décontracté.'),
    ('T-shirt Col V', 'tshirt-col-v', 'T-shirt col V, coupe ajustée, 100% coton.'),
    ('T-shirt Manches Longues', 'tshirt-manches-longues', 'T-shirt à manches longues, idéal pour la mi-saison.'),
    ('T-shirt Sport Respirant', 'tshirt-sport-respirant', 'Tissu technique respirant, conçu pour le sport.'),
    ('T-shirt Vintage', 'tshirt-vintage', 'Look délavé vintage, coupe régulière.'),
    ('T-shirt Slim Fit', 'tshirt-slim-fit', 'Coupe slim ajustée, 100% coton peigné.'),
    ('T-shirt Bio Recyclé', 'tshirt-bio-recycle', 'Fabriqué à partir de coton recyclé, démarche éco-responsable.'),
    ('T-shirt Graphique Minimaliste', 'tshirt-graphique-minimaliste', 'Design minimaliste imprimé, coupe classique.')
) AS v(name, slug, description)
CROSS JOIN categories c
WHERE c.slug = 'vetements';

-- Déclinaisons couleur : SKU préfixe + prix par produit, 3 couleurs chacun.
INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-NOIR', v.price, 20, true, '{"couleur": "Noir"}'::jsonb, p.id, now(), now()
FROM (VALUES
    ('tshirt-essentiel', 'ESS', 14.99),
    ('tshirt-premium-coton-bio', 'PCB', 24.99),
    ('tshirt-oversize', 'OVS', 22.99),
    ('tshirt-col-v', 'COLV', 17.99),
    ('tshirt-manches-longues', 'MLNG', 26.99),
    ('tshirt-sport-respirant', 'SPRT', 21.99),
    ('tshirt-vintage', 'VNTG', 23.99),
    ('tshirt-slim-fit', 'SLIM', 19.99),
    ('tshirt-bio-recycle', 'RECY', 27.99),
    ('tshirt-graphique-minimaliste', 'GRPH', 25.99)
) AS v(slug, sku_prefix, price)
JOIN products p ON p.slug = v.slug;

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-BLANC', v.price, 20, true, '{"couleur": "Blanc"}'::jsonb, p.id, now(), now()
FROM (VALUES
    ('tshirt-essentiel', 'ESS', 14.99),
    ('tshirt-premium-coton-bio', 'PCB', 24.99),
    ('tshirt-oversize', 'OVS', 22.99),
    ('tshirt-col-v', 'COLV', 17.99),
    ('tshirt-manches-longues', 'MLNG', 26.99),
    ('tshirt-sport-respirant', 'SPRT', 21.99),
    ('tshirt-vintage', 'VNTG', 23.99),
    ('tshirt-slim-fit', 'SLIM', 19.99),
    ('tshirt-bio-recycle', 'RECY', 27.99),
    ('tshirt-graphique-minimaliste', 'GRPH', 25.99)
) AS v(slug, sku_prefix, price)
JOIN products p ON p.slug = v.slug;

INSERT INTO product_variants (sku, price, stock, active, attributes, product_id, created_at, updated_at)
SELECT v.sku_prefix || '-ROUGE', v.price, 20, true, '{"couleur": "Rouge"}'::jsonb, p.id, now(), now()
FROM (VALUES
    ('tshirt-essentiel', 'ESS', 14.99),
    ('tshirt-premium-coton-bio', 'PCB', 24.99),
    ('tshirt-oversize', 'OVS', 22.99),
    ('tshirt-col-v', 'COLV', 17.99),
    ('tshirt-manches-longues', 'MLNG', 26.99),
    ('tshirt-sport-respirant', 'SPRT', 21.99),
    ('tshirt-vintage', 'VNTG', 23.99),
    ('tshirt-slim-fit', 'SLIM', 19.99),
    ('tshirt-bio-recycle', 'RECY', 27.99),
    ('tshirt-graphique-minimaliste', 'GRPH', 25.99)
) AS v(slug, sku_prefix, price)
JOIN products p ON p.slug = v.slug;

-- Une photo par couleur, réutilisée sur les 10 produits, liée à la
-- déclinaison correspondante. Le Noir sert d'image principale (miniature
-- catalogue) pour chaque produit.
INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&h=600&fit=crop', 0, true, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-NOIR' AND pv.sku NOT LIKE 'TS-%';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1778671394516-8270eac13c42?w=600&h=600&fit=crop', 1, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-BLANC' AND pv.sku NOT LIKE 'TS-%';

INSERT INTO product_images (url, position, is_primary, product_id, variant_id, created_at, updated_at)
SELECT 'https://images.unsplash.com/photo-1624373607006-348f61ea2d76?w=600&h=600&fit=crop', 2, false, pv.product_id, pv.id, now(), now()
FROM product_variants pv
WHERE pv.sku LIKE '%-ROUGE' AND pv.sku NOT LIKE 'TS-%';
