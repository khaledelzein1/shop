-- Rôles de référence nécessaires à l'inscription (ROLE_USER) et à
-- l'espace admin (ROLE_ADMIN). Données de référence versionnées avec le
-- schéma plutôt qu'insérées par du code applicatif, pour éviter toute
-- course/duplication au démarrage.

INSERT INTO roles (name, created_at, updated_at) VALUES
    ('ROLE_USER', now(), now()),
    ('ROLE_ADMIN', now(), now());
