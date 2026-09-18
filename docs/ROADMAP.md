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
  - [ ] ⚠️ Démarrage local complet non vérifié de bout en bout : JDK 21 pas
        encore installé sur la machine (compilation validée avec
        `-Dmaven.compiler.release=17` en attendant) et Docker Desktop pas
        lancé au moment du test. À revalider dès que les deux sont prêts.
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
  - [ ] ⚠️ Non vérifié de bout en bout (register → login → endpoint protégé)
        faute de JDK 21 et de Docker Desktop actifs sur la machine au moment
        du test — code compilé et relu, à valider dès que possible
- [x] **Étape 3 — Catalogue (API)**
  - [x] CRUD catégories (`/api/categories`, lecture publique, mutation ADMIN)
  - [x] CRUD produits (`/api/admin/products`) + variantes et images
        (sous-endpoints dédiés `/api/admin/products/{id}/variants|images`)
  - [x] Recherche publique `/api/products` : filtres catégorie/marque/prix/
        disponibilité/texte via `Specification`, pagination + tri (`Pageable`)
  - [x] Données de démo (`V3__seed_catalog_demo_data.sql`) : 2 catégories,
        2 produits avec variantes JSONB (RAM/stockage, taille/couleur) et images
  - [ ] ⚠️ Non vérifié de bout en bout (mêmes blocages JDK 21 / Docker Desktop
        que l'étape 2) — code compilé et relu, à valider dès que possible
- [ ] **Étape 4 — Panier & Checkout (API)**
  - [ ] Ajout/modification/suppression d'articles panier
  - [ ] Checkout simulé → création de commande
  - [ ] Historique des commandes utilisateur
- [ ] **Étape 5 — Admin (API)**
  - [ ] Gestion commandes (changement de statut)
  - [ ] Gestion utilisateurs
  - [ ] Gestion stock
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
