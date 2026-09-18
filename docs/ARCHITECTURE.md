# Décisions d'architecture

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

## 6. Frontend

Angular avec architecture par feature modules (`core/`, `shared/`,
`features/auth`, `features/catalog`, `features/cart`, `features/checkout`,
`features/admin`...), lazy loading des routes, intercepteur HTTP pour le
JWT, guards pour les routes protégées/admin. Détail complet à l'étape
frontend (voir [ROADMAP](ROADMAP.md)) — pas figé dès maintenant pour ne pas
décider hors contexte du backend.
