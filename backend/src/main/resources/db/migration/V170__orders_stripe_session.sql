-- Paiement par carte (Stripe Checkout) : chaque commande garde l'id de la
-- session de paiement Stripe ouverte au checkout. C'est cet id — et non ce
-- que renvoie le navigateur — qui sert à vérifier auprès de Stripe que la
-- commande a bien été payée. NULL pour les commandes passées avant.

ALTER TABLE orders ADD COLUMN stripe_session_id VARCHAR(255);

CREATE UNIQUE INDEX uk_orders_stripe_session_id ON orders (stripe_session_id);

-- Le job de réconciliation cherche les commandes PENDING anciennes.
CREATE INDEX idx_orders_status_created_at ON orders (status, created_at);
