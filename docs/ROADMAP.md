# Roadmap

Construction incrémentale, une étape validée à la fois. On ne génère jamais
tout le projet d'un coup.

- [x] **Étape 0 — Cadrage**
  - [x] Choix d'architecture (monolithe modulaire, package-by-feature)
  - [x] Modèle de domaine MVP / V2
  - [x] Structure du repo, `.gitignore`, README
- [ ] **Étape 1 — Squelette backend**
  - [ ] Projet Maven Spring Boot (Java 21)
  - [ ] Config PostgreSQL + Flyway
  - [ ] `BaseEntity`, entités MVP (`User`, `Role`, `Category`, `Product`,
        `ProductVariant`, `ProductImage`)
  - [ ] Première migration Flyway + démarrage local (Docker Compose Postgres)
- [ ] **Étape 2 — Sécurité**
  - [ ] Spring Security + JWT (login, register, refresh)
  - [ ] Rôles USER / ADMIN, endpoints protégés
- [ ] **Étape 3 — Catalogue (API)**
  - [ ] CRUD catégories (admin) / lecture publique
  - [ ] CRUD produits + variantes (admin)
  - [ ] Recherche, filtres (catégorie, prix, marque, disponibilité), pagination/tri
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
