-- Nom d'utilisateur optionnel, utilisable à la place de l'email pour se
-- connecter (choisi par l'admin depuis son espace « Account »). Unicité
-- insensible à la casse : « Admin » et « admin » désignent le même compte.

ALTER TABLE users ADD COLUMN username VARCHAR(30);

CREATE UNIQUE INDEX users_username_lower_key ON users (lower(username));
