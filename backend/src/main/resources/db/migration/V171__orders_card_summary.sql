-- Résumé de la carte utilisée pour payer ("Visa •••• 4242"), affiché dans le
-- détail de la commande. Jamais le numéro complet ni le CVC.

ALTER TABLE orders ADD COLUMN card_brand VARCHAR(30);
ALTER TABLE orders ADD COLUMN card_last4 VARCHAR(4);
