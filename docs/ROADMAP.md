# Roadmap

Construction incrémentale, une étape validée à la fois. On ne génère jamais
tout le projet d'un coup.

- [x] **Étape 0 — Cadrage**
  - [x] Choix d'architecture (monolithe modulaire, package-by-feature)
  - [x] Modèle de domaine MVP / V2
  - [x] Structure du repo, `.gitignore`, README
- [x] **Étape 1 — Squelette backend**
  - [x] Projet Maven Spring Boot (Java 21) — code compilé et validé (voir note JDK ci-dessous)
  - [x] Config PostgreSQL + Flyway (`application.yml`, `db/migration/V1__init_schema.sql`)
  - [x] `BaseEntity`, entités MVP (`User`, `Role`, `Category`, `Product`,
        `ProductVariant`, `ProductImage`)
  - [x] `docker-compose.yml` Postgres local (`docker/docker-compose.yml`)
  - [x] ✅ Vérifié de bout en bout le 2026-09-18 (JDK 21 Corretto + Docker
        Desktop) : `docker compose up` + `mvn spring-boot:run`, 5 migrations
        Flyway appliquées, démarrage propre
- [x] **Étape 2 — Sécurité**
  - [x] Spring Security + JWT stateless (`/api/auth/register`, `/api/auth/login`) —
        pas de refresh token en MVP, expiration 24h (voir compromis dans
        `ARCHITECTURE.md`, rotation prévue en étape 10)
  - [x] Rôles USER / ADMIN (seed via `V2__seed_roles.sql`), `@EnableMethodSecurity`
        pour protéger les futurs endpoints admin avec `@PreAuthorize`
  - [x] Gestion d'erreurs globale (`GlobalExceptionHandler` + `ApiError`)
  - [x] Swagger UI (springdoc) avec bouton "Authorize" JWT
  - [x] Compte admin de démo auto-créé au démarrage (`AdminAccountInitializer`,
        désactivable via `SEED_ADMIN_ENABLED=false`)
  - [x] ✅ Vérifié de bout en bout : register (201 + token), login admin
        (201 + token ROLE_ADMIN), accès admin refusé pour un USER (403)
- [x] **Étape 3 — Catalogue (API)**
  - [x] CRUD catégories (`/api/categories`, lecture publique, mutation ADMIN)
  - [x] CRUD produits (`/api/admin/products`) + variantes et images
        (sous-endpoints dédiés `/api/admin/products/{id}/variants|images`)
  - [x] Recherche publique `/api/products` : filtres catégorie/marque/prix/
        disponibilité/texte via `Specification`, pagination + tri (`Pageable`)
  - [x] Données de démo (`V3__seed_catalog_demo_data.sql`) : 2 catégories,
        2 produits avec variantes JSONB (RAM/stockage, taille/couleur) et images
  - [x] ✅ Vérifié de bout en bout — a révélé un vrai bug corrigé dans la
        foulée : les GET publics `/api/categories` et `/api/products`
        étaient bloqués (403) car non listés dans les endpoints publics du
        `SecurityFilterChain` (seul `@PreAuthorize` protégeait les écritures,
        mais `anyRequest().authenticated()` bloquait aussi les lectures).
        Corrigé avec un `requestMatchers(HttpMethod.GET, ...).permitAll()`
        dédié dans `SecurityConfig`.
- [x] **Étape 4 — Panier & Checkout (API)**
  - [x] Carnet d'adresses (`/api/me/addresses`) — nécessaire au checkout,
        pas prévu comme étape backend dédiée dans la roadmap initiale mais
        indispensable ici (une seule adresse par défaut, gérée en base)
  - [x] Panier (`/api/me/cart`) : ajout (fusion de quantité si variante déjà
        présente), modification, suppression — création à la volée, vérifie
        le stock disponible à chaque opération
  - [x] Checkout simulé (`POST /api/me/orders/checkout`) → décrémente le
        stock, snapshotte produits/adresse, crée la commande en `CONFIRMED`
  - [x] Historique des commandes (`GET /api/me/orders`, `GET /api/me/orders/{id}`),
        paginé, trié par date décroissante
  - [x] ✅ Vérifié de bout en bout : ajout panier (fusion quantité), rejet
        409 si stock insuffisant, checkout → commande `CONFIRMED`, stock
        variante décrémenté (15→13 vérifié), panier vidé après commande,
        historique correct
- [x] **Étape 5 — Admin (API)**
  - [x] Gestion commandes (`/api/admin/orders`) : liste filtrable (statut,
        utilisateur) paginée, détail, `PATCH .../status` avec machine à
        états simple (transitions autorisées uniquement, ex. impossible de
        repasser `SHIPPED` en `PENDING`) — annuler restitue le stock
  - [x] Gestion utilisateurs (`/api/admin/users`) : liste filtrable par
        email, détail, activation/désactivation — un admin ne peut pas se
        désactiver lui-même (409)
  - [x] Gestion stock : `PATCH /api/admin/products/{id}/variants/{id}/stock`
        dédié, en plus du `PUT` variante complet
  - [x] Fix sécurité complémentaire : un compte désactivé perd l'accès
        immédiatement même avec un JWT déjà émis (vérification `isEnabled()`
        à chaque requête dans `JwtAuthenticationFilter`, pas seulement au login)
  - [x] ✅ Vérifié de bout en bout — a révélé un 2e bug corrigé dans la
        foulée : la désactivation d'un compte pendant la tentative de login
        suivante renvoyait 500 (`DisabledException` non gérée par
        `GlobalExceptionHandler`) au lieu d'un 401 propre. Corrigé.
- [ ] **Étape 6 — Frontend Angular**
  - [ ] Squelette (routing, core/shared/features, intercepteur JWT)
  - [ ] Auth (login/register)
  - [ ] Catalogue + détail produit + filtres
  - [ ] Panier + checkout
  - [ ] Profil, adresses, historique commandes
  - [ ] Espace admin
- [ ] **Étape 7 — DevOps**
  - [ ] Dockerfile backend, Dockerfile frontend
  - [ ] docker-compose (app + Postgres)
  - [ ] CI GitHub Actions (build, tests, lint)
  - [ ] Déploiement cloud (à définir : Render/Railway/Fly.io pour un portfolio gratuit)
- [ ] **Étape 8 — Observabilité**
  - [ ] Spring Actuator (health, info)
  - [ ] Logs structurés
  - [ ] Métriques de base (Prometheus-ready)
- [ ] **Étape 9 — Qualité**
  - [ ] Tests unitaires (services)
  - [ ] Tests d'intégration (Testcontainers + Postgres)
  - [ ] Lint (Checkstyle/Spotless backend, ESLint frontend)
  - [ ] Documentation OpenAPI/Swagger
- [ ] **Étape 10 — Polish production-ready**
  - [ ] Rate limiting, cache, refresh token rotation
  - [ ] Amélioration pagination/recherche (ex. full-text search)
  - [ ] Revue sécurité finale

## V2 (après le MVP complet)
- [ ] Wishlist
- [ ] Reviews / avis produits
- [ ] Promotions / codes promo
