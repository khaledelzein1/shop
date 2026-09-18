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
- [x] **Étape 6 — Frontend Angular**
  - [x] Squelette Angular 22 standalone (`core/`, `shared/`, `features/`),
        intercepteurs JWT + gestion 401, guards `authGuard`/`adminGuard`
  - [x] Auth (login/register), état utilisateur réactif via signals
  - [x] Catalogue (filtres catégorie/recherche/stock, pagination) + détail
        produit (sélecteur de variante, ajout au panier)
  - [x] Panier (quantité, suppression) + checkout (sélection d'adresse,
        récapitulatif, confirmation)
  - [x] Profil : carnet d'adresses (ajout/suppression), historique des
        commandes (liste + détail)
  - [x] ✅ Vérifié de bout en bout avec Playwright headless contre le vrai
        backend : home → catalogue → détail produit → inscription → ajout
        panier → panier → ajout adresse → checkout → confirmation →
        historique. Zéro erreur console sur tout le parcours. Captures
        d'écran validées visuellement.
  - [x] Espace admin (`/admin`, guard `adminGuard`) : dashboard, CRUD
        catégories, CRUD produits avec gestion variantes (ajout, stock
        inline, suppression) et images, gestion des commandes (changement
        de statut par menu déroulant), gestion des utilisateurs
        (recherche, activation/désactivation)
  - [x] ✅ Vérifié de bout en bout avec Playwright headless (connexion admin,
        création catégorie/produit, ajout variante + image, mise à jour du
        stock en ligne, changement de statut de commande) — zéro erreur
        console
  - [x] Petit ajustement backend en cours de route : ajout du champ
        `active` à `ProductSummaryResponse` (absent jusque-là), nécessaire
        pour que la liste admin distingue produits actifs/inactifs
- [x] **Étape 7 — DevOps** (déploiement cloud reporté, reste de l'étape faite)
  - [x] `backend/Dockerfile` (multi-stage Maven → JRE Alpine, utilisateur
        non-root) et `frontend/Dockerfile` (multi-stage build Angular →
        nginx Alpine)
  - [x] `docker-compose.yml` (dev, Postgres seul) + `docker-compose.prod.yml`
        (pile complète Postgres + backend + frontend/nginx avec reverse
        proxy `/api`) — voir `docker/README.md`
  - [x] CI GitHub Actions (`.github/workflows/ci.yml`) : build+tests backend
        (JDK 21), build frontend (Node 24), build des deux images Docker
  - [x] ✅ Vérifié de bout en bout : les deux images buildent, la pile
        complète tourne (`docker compose -f docker-compose.prod.yml up`),
        parcours client entier rejoué avec Playwright contre le build de
        prod servi par nginx — zéro erreur console
  - [x] 🐛 Bug DevOps trouvé et corrigé pendant la validation : les deux
        fichiers compose partageant le même dossier, ils héritaient du même
        nom de projet Docker par défaut et donc du **même volume Postgres
        nommé** (`pgdata`) — le second à démarrer réutilisait les données
        (et donc l'ancien mot de passe) du premier, causant un échec
        d'authentification. Corrigé en donnant un `name:` de projet
        explicite et distinct à chaque fichier compose (`shop-dev` /
        `shop-prod`), qui isole aussi les volumes.
  - [ ] Déploiement cloud (Render/Railway/Fly.io) — reporté : nécessite un
        compte/service externe, décision à prendre avec l'utilisateur avant
        de continuer
- [x] **Étape 8 — Observabilité**
  - [x] Spring Actuator : `/actuator/health` et `/actuator/info` publics
        (avec build-info via `spring-boot-maven-plugin`), `/actuator/metrics`
        et `/actuator/prometheus` réservés `ROLE_ADMIN`
  - [x] Logs structurés : `logback-spring.xml` avec profil dev (pattern
        lisible + id de corrélation) vs profil prod (JSON via
        `logstash-logback-encoder`) ; `RequestIdFilter` associe un id
        unique à chaque requête (repris de `X-Request-Id` si fourni par un
        proxy amont, sinon généré), placé en MDC et renvoyé dans la réponse
  - [x] Métriques Prometheus-ready via `micrometer-registry-prometheus`
  - [x] ✅ Vérifié : health/info accessibles sans auth, metrics/prometheus
        bloqués sans token (403) puis accessibles en ADMIN, header
        `X-Request-Id` bien renvoyé sur chaque réponse
- [ ] **Étape 9 — Qualité** (tests faits, lint et doc OpenAPI restants)
  - [x] Tests unitaires backend (JUnit 5 + Mockito) : `AuthServiceTest`,
        `CartServiceTest`, `OrderServiceTest` — logique métier clé (hash de
        mot de passe + rôle à l'inscription, refus de stock insuffisant,
        fusion de quantité panier, machine à états de statut commande,
        restitution de stock à l'annulation). **8/8 verts**, rapides, sans
        Docker (`mvn test`)
  - [x] Tests d'intégration backend (Testcontainers + vrai Postgres, MockMvc) :
        `AuthIT` (register/login, email dupliqué, validation, accès public
        catalogue, 403 admin sans auth) et `CheckoutIT` (parcours complet
        panier → adresse → checkout → stock décrémenté → panier vidé →
        historique, panier vide → 409, stock insuffisant → 409 et aucune
        commande créée)
  - [x] Séparation Maven standard : Surefire (`*Test.java`, phase `test`,
        rapide, pas de Docker requis) vs Failsafe (`*IT.java`, phase
        `verify`, Testcontainers) — `mvn test` reste utilisable au
        quotidien même sans Docker qui tourne
  - [x] Tests frontend (Vitest) : `AuthService` (login/logout, signals
        `isAuthenticated`/`isAdmin`), `authGuard`/`adminGuard` (redirections),
        `CartService` (`itemCount` calculé). **10/10 verts**
  - [x] Jacoco (rapport de couverture backend) ; CI mise à jour pour lancer
        les tests frontend (`ng test --watch=false`) en plus du build
  - [ ] ⚠️ Limitation constatée : les tests d'intégration backend (`*IT`,
        Testcontainers) ne se lancent pas depuis cette machine Windows —
        Docker Desktop répond mais son named pipe (`npipe:////./pipe/...`)
        renvoie une réponse HTTP 400 inattendue au client Docker de
        Testcontainers (essayé : `DOCKER_HOST` explicite, mise à jour
        Testcontainers 1.20.2 → 1.21.3, `~/.testcontainers.properties`,
        exploration WSL2 — aucun n'a résolu le problème). C'est un souci
        d'interopérabilité Windows/Docker Desktop connu, pas un défaut du
        code : `mvn verify` (CI, runners Linux) devrait fonctionner
        nativement puisque c'est l'environnement de référence de
        Testcontainers, **mais ceci n'a pas encore été vérifié
        empiriquement** faute de remote GitHub configuré sur ce repo. À
        confirmer dès qu'un remote existe et qu'un premier push déclenche
        la CI.
  - [ ] Lint (Checkstyle/Spotless backend, ESLint frontend)
  - [ ] Documentation OpenAPI/Swagger (au-delà de l'exposition Swagger UI
        déjà en place depuis l'étape 2 — descriptions/exemples détaillés)
- [ ] **Étape 10 — Polish production-ready**
  - [ ] Rate limiting, cache, refresh token rotation
  - [ ] Amélioration pagination/recherche (ex. full-text search)
  - [ ] Revue sécurité finale

## V2 (après le MVP complet)
- [ ] Wishlist
- [ ] Reviews / avis produits
- [ ] Promotions / codes promo
