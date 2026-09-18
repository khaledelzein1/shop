-- Filet de sécurité : même si un bug applicatif ou une race condition laissait passer une
-- décrémentation en trop, la base refuse catégoriquement un stock négatif.
ALTER TABLE product_variants ADD CONSTRAINT chk_stock_non_negative CHECK (stock >= 0);

-- Verrouillage optimiste (@Version) : deux checkouts concurrents sur la même variante à stock
-- limité ne doivent pas tous les deux réussir à décrémenter sur la base d'une lecture périmée.
ALTER TABLE product_variants ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
