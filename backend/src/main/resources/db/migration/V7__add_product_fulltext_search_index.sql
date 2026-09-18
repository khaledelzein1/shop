-- Index fonctionnel GIN pour la recherche plein texte sur le catalogue (nom + marque +
-- description). L'expression doit être rigoureusement identique côté requête pour que le
-- planificateur Postgres utilise cet index plutôt que de recalculer to_tsvector ligne par ligne.
CREATE INDEX idx_products_fulltext ON products
    USING gin (to_tsvector('french', coalesce(name, '') || ' ' || coalesce(brand, '') || ' ' || coalesce(description, '')));
