# Modèle de domaine

## Entités MVP

### User
- `id`, `email` (unique), `passwordHash`, `firstName`, `lastName`,
  `enabled`, `createdAt`, `updatedAt`
- Relation `ManyToMany` → `Role`
- Relation `OneToMany` → `Address`

### Role
- `id`, `name` (`ROLE_USER`, `ROLE_ADMIN`)

### Category
- `id`, `name`, `slug` (unique), `description`
- Flat en MVP (pas de sous-catégories). Passage à une hiérarchie
  (`parent` auto-référencé) en V2 si besoin — évite de sur-modéliser
  avant d'avoir un vrai besoin de navigation à facettes profonde.

### Product
- `id`, `name`, `slug`, `description`, `brand`, `active` (bool)
- Relation `ManyToOne` → `Category`
- Relation `OneToMany` → `ProductVariant`
- Relation `OneToMany` → `ProductImage`
- `createdAt`, `updatedAt`

### ProductVariant
- `id`, `sku` (unique), `price` (BigDecimal), `stock` (int), `active` (bool)
- `attributes` : JSONB `Map<String,String>` — ex. vêtement
  `{"taille":"M","couleur":"Noir"}`, informatique
  `{"ram":"16Go","stockage":"512Go SSD","couleur":"Gris sidéral"}`
- Relation `ManyToOne` → `Product`

### ProductImage
- `id`, `url`, `position` (int, ordre d'affichage), `isPrimary` (bool)
- Relation `ManyToOne` → `Product`

### Cart
- `id`, `createdAt`, `updatedAt`
- Relation `OneToOne` → `User` (un panier actif par utilisateur)
- Relation `OneToMany` → `CartItem`

### CartItem
- `id`, `quantity`, `unitPriceSnapshot` (prix au moment de l'ajout)
- Relation `ManyToOne` → `Cart`
- Relation `ManyToOne` → `ProductVariant`

> **Pourquoi un `unitPriceSnapshot`** : si le prix change après ajout au
> panier, on veut pouvoir afficher explicitement l'écart au client avant
> le checkout plutôt que de recalculer silencieusement — meilleure UX et
> plus honnête.

### Order
- `id`, `status` (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`,
  `CANCELLED`), `totalAmount`, `createdAt`
- Relation `ManyToOne` → `User`
- Relation `OneToMany` → `OrderItem`
- Adresse de livraison : snapshot des champs (pas de FK vers `Address`,
  voir note ci-dessous)

### OrderItem
- `id`, `productName` (snapshot), `sku` (snapshot), `quantity`,
  `unitPrice` (snapshot au moment de la commande)
- Relation `ManyToOne` → `Order`
- Relation `ManyToOne` → `ProductVariant` (référence, pour navigation,
  mais jamais utilisée pour recalculer le prix a posteriori)

> **Pourquoi snapshotter les données produit dans `OrderItem` et
> l'adresse dans `Order`** : une commande passée doit rester historiquement
> exacte même si le produit est renommé/supprimé ou l'adresse modifiée
> ensuite. C'est une règle standard en e-commerce : *une commande est un
> document figé*, pas une vue live sur le catalogue.

### Address
- `id`, `label` (ex. "Domicile"), `street`, `city`, `zipCode`, `country`,
  `isDefault` (bool)
- Relation `ManyToOne` → `User`

## Entités V2 (repoussées volontairement)

| Entité | Rôle | Pourquoi en V2 |
|---|---|---|
| `Wishlist` | Liste de produits favoris par utilisateur | Feature additive, ne touche pas au cœur transactionnel |
| `Review` | Avis produit (note + commentaire) noté par `User` sur `Product` | Nécessite modération admin, pas critique pour démontrer la maîtrise technique du cœur (auth, catalogue, panier, commande) |
| `Promotion` | Code promo / réduction (pourcentage, montant fixe, conditions de validité) | Impacte le calcul de prix du panier/commande — mieux vaut l'ajouter une fois le flux panier→commande stable et testé |

Le fait de les ajouter **après coup** sur un monolithe modulaire déjà en
place est en soi une bonne démonstration de maintenabilité pour un
entretien ("comment as-tu fait évoluer le modèle sans casser l'existant").

## Diagramme relationnel simplifié (MVP)

```
User ──< Address
User ──1:1── Cart ──< CartItem >── ProductVariant
User ──< Order ──< OrderItem >── ProductVariant (référence)
User >──< Role

Category ──< Product ──< ProductVariant
Product ──< ProductImage
```
