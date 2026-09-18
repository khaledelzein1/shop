# Décisions d'architecture

## 0. Environnement de build requis

**JDK 21 exactement** (pas une version plus récente comme 25 au moment de
la rédaction) : Lombok, même en version très récente (testé jusqu'à
1.18.38), ne patchait pas encore correctement les internals du compilateur
d'un JDK 25 tout juste sorti — tous les getters/setters générés par
Lombok échouaient à la compilation (`cannot find symbol`). Le projet
cible `java.version=21` dans `backend/pom.xml` ; utiliser exactement un
JDK 21 (ex. Corretto 21, Temurin 21) évite ce genre de désagrément avec
les outils qui n'ont pas toujours immédiatement supporté les JDK les
plus récents.

## 1. Monolithe modulaire (pas de microservices)

**Décision** : un seul déployable Spring Boot, découpé en modules métier internes.

**Pourquoi** : pour un projet solo / portfolio, les microservices ajoutent une
complexité (orchestration, réseau, cohérence distribuée, observabilité
distribuée) qui n'apporte rien tant qu'il n'y a pas de vraie raison
d'échelle ou d'équipes séparées. Un monolithe **bien modularisé** est plus
crédible aux yeux d'un recruteur qu'un monolithe distribué mal justifié —
ça montre qu'on sait quand *ne pas* sur-ingénierier.

**Compromis assumé** : si le projet devait un jour scaler en vraie
production avec plusieurs équipes, on découperait `catalog`, `order`,
`user` en services séparés. Ce n'est pas nécessaire ici, et le découpage
en modules internes rend cette évolution possible plus tard sans réécriture
complète (faible couplage entre modules dès le départ).

## 2. Build : Maven

Choix standard en entreprise, très lisible pour un recruteur backend Java.

## 3. Découpage backend : package-by-feature, layered en interne

```
com.shop
├── ShopApplication.java
├── config/                # SecurityConfig, OpenApiConfig, JacksonConfig, CorsConfig
├── common/
│   ├── exception/          # exceptions métier + @ControllerAdvice global
│   ├── domain/              # BaseEntity (id, createdAt, updatedAt)
│   └── dto/                 # PageResponse, ApiError, etc.
├── security/
│   ├── jwt/                 # JwtService, JwtAuthFilter
│   └── config/               # UserDetailsService custom
├── user/
│   ├── User.java, Role.java
│   ├── UserController, UserService, UserRepository
│   └── dto/ (RegisterRequest, UserResponse, ...)
├── address/
├── catalog/
│   ├── Category.java, Product.java, ProductVariant.java, ProductImage.java
│   ├── controller/, service/, repository/, dto/, mapper/
├── cart/
│   ├── Cart.java, CartItem.java
│   └── ...
└── order/
    ├── Order.java, OrderItem.java, OrderStatus.java
    └── ...
```

**Pourquoi package-by-feature plutôt que package-by-layer** (`controller/`,
`service/`, `repository/` au niveau racine) : ça reste lisible même quand
le projet grossit (on ouvre `catalog/` et on a tout ce qui concerne le
catalogue), et ça matérialise les frontières entre modules métier — un
module ne doit dépendre que de l'API publique d'un autre module, jamais
de ses détails internes (repository d'un autre module par ex.).

À l'intérieur de chaque module, on garde quand même la séparation classique
en couches : `Controller → Service → Repository`, avec DTO en entrée/sortie
(jamais d'entité JPA exposée directement dans l'API).

## 4. Règles transverses

- **DTO obligatoires** pour toute entrée/sortie d'API — jamais d'entité JPA
  sérialisée directement (évite les fuites de champs internes, les cycles
  de sérialisation, et découple le modèle de persistance du contrat API).
- **Validation** avec Bean Validation (`@Valid`, `jakarta.validation`) sur
  les DTO d'entrée.
- **Gestion d'erreurs centralisée** via `@ControllerAdvice` : format d'erreur
  JSON unique (`ApiError` : timestamp, status, code, message, path,
  éventuellement liste d'erreurs de validation par champ).
- **Pagination/tri/filtres** : `Pageable` de Spring Data sur tous les
  endpoints de liste (catalogue, commandes admin, utilisateurs admin).
- **Sécurité** : Spring Security + JWT stateless, rôles `ROLE_USER` /
  `ROLE_ADMIN`, endpoints admin protégés par `@PreAuthorize`.
- **Migrations de schéma** : Flyway, jamais de `ddl-auto: update` en dehors
  du dev local rapide (et encore, on préfère Flyway dès le début pour
  prendre les bons réflexes).

## 5. Modélisation des variantes produit (informatique vs vêtements)

**Décision** : `ProductVariant` porte les champs structurés communs (`sku`,
`price`, `stock`, `active`) + une colonne **JSONB** `attributes`
(`Map<String, String>`) pour les caractéristiques spécifiques à la
catégorie (`taille`, `couleur` pour un vêtement ; `ram`, `stockage`,
`configuration` pour de l'informatique).

**Pourquoi pas une table EAV (clé/valeur normalisée)** : plus "propre" en
théorie relationnelle, mais complique fortement les requêtes, la
validation et les jointures pour un gain marginal ici.

**Pourquoi pas une hiérarchie d'héritage JPA** (`ClothingVariant extends
ProductVariant`, `ComputerVariant extends ProductVariant`) : rigide dès
qu'on ajoute une 3e catégorie de produits, et complique le mapping
JPA (stratégies `JOINED`/`SINGLE_TABLE`) pour un bénéfice limité en V1.

**Compromis assumé** : les attributs en JSONB ne sont pas typés/validés au
niveau base de données — la validation se fait côté application (DTO +
règles par catégorie). C'est un choix pragmatique très utilisé en vrai
e-commerce (attributs dynamiques par catégorie), qu'on assume et qu'on sait
expliquer en entretien plutôt que de le cacher.

## 7. API catalogue : recherche, pagination, endpoints public vs admin

**Filtres dynamiques** : `ProductSpecifications` (Spring Data JPA
`Specification`) compose les prédicats (catégorie, marque, fourchette de
prix, disponibilité, recherche texte) uniquement pour les filtres
réellement fournis — plus lisible et testable qu'une méthode de repository
avec dix paramètres optionnels ou une explosion de `findByXAndYAndZ`.

**Pagination + N+1** : la recherche paginée interroge `Product` sans fetch
join (`Specification` + `Pageable` standard). Pour éviter le N+1 sur les
collections `variants`/`images` utilisées par la vue liste (prix min/max,
disponibilité, image principale), on s'appuie sur `@BatchSize(20)` côté
entité plutôt que sur des fetch joins manuels : Hibernate regroupe alors le
chargement des collections d'une page entière en une requête `IN (...)`
au lieu d'une requête par produit.

**Compromis assumé** : à l'échelle d'un catalogue de quelques milliers de
produits, cette approche reste largement suffisante. Une vraie recherche à
fort volume (facettes, tri par pertinence texte) passerait par un moteur
dédié (Postgres full-text, ou Elasticsearch) — hors scope MVP.

**Endpoints publics vs admin séparés pour les produits**
(`/api/products` vs `/api/admin/products`) : contrairement aux catégories
(simple donnée de référence, un seul contrôleur avec `@PreAuthorize` par
méthode), les produits ont un statut actif/inactif et l'admin doit pouvoir
lister/consulter les produits désactivés — un besoin que l'endpoint public
n'a jamais. Séparer les chemins évite de faire fuiter de la logique
d'autorisation dans les filtres de recherche.

**Variantes et images gérées hors du payload produit** : `ProductRequest`
ne contient ni variantes ni images — elles ont leurs propres endpoints
(`POST /api/admin/products/{id}/variants`, `.../images`). Évite une
logique de diff complexe (ajout/suppression/mise à jour en un seul PUT) et
colle à un modèle REST plus simple : un sous-endpoint par sous-ressource.

## 9. Panier, checkout et historique — convention `/api/me/**`

**Convention d'URL** : toutes les ressources qui appartiennent à
l'utilisateur connecté (adresses, panier, commandes) sont exposées sous
`/api/me/...` plutôt que par id dans l'URL (`/api/users/{id}/cart`). L'id
vient toujours du token JWT (`@AuthenticationPrincipal`), jamais d'un
paramètre — impossible pour un utilisateur de manipuler le panier ou les
commandes d'un autre en changeant un id dans l'URL.

**Panier créé à la volée** : pas d'endpoint `POST /api/me/cart` — le
panier est créé automatiquement au premier ajout d'article
(`CartService.getOrCreateCart`). Une ligne panier par variante ; ajouter
une variante déjà présente incrémente la quantité plutôt que de dupliquer
la ligne (contrainte unique `(cart_id, variant_id)` en base pour le
garantir aussi côté données).

**Checkout simulé → statut `CONFIRMED` immédiat** : sans vraie passerelle
de paiement, un statut `PENDING` initial n'aurait de sens que pour
représenter une attente de confirmation de paiement — absente ici. Le
checkout va donc directement à `CONFIRMED` après validation du stock. Le
stock, lui, est réellement décrémenté (pas simulé) : c'est la partie
métier qu'on veut démontrer proprement. Les transitions suivantes
(`SHIPPED`, `DELIVERED`, `CANCELLED`) seront pilotées par l'admin
(étape 5).

**Adresse de livraison** : le checkout référence une adresse existante du
carnet d'adresses de l'utilisateur (`addressId`) plutôt que de ressaisir
les champs — cohérent avec la fonctionnalité "gestion des adresses" et
évite de dupliquer la validation des champs d'adresse dans deux DTO.

## 10. Frontend

Angular avec architecture par feature modules (`core/`, `shared/`,
`features/auth`, `features/catalog`, `features/cart`, `features/checkout`,
`features/admin`...), lazy loading des routes, intercepteur HTTP pour le
JWT, guards pour les routes protégées/admin. Détail complet à l'étape
frontend (voir [ROADMAP](ROADMAP.md)) — pas figé dès maintenant pour ne pas
décider hors contexte du backend.
