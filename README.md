# Shop — Plateforme e-commerce multi-catégories

Projet portfolio fullstack : vente de matériel informatique et de vêtements.
Monolithe modulaire Spring Boot + Angular, pensé pour être proche d'un vrai
contexte d'entreprise (architecture, sécurité, tests, CI/CD, observabilité).

## Stack

| Domaine     | Techno                                  |
|-------------|------------------------------------------|
| Backend     | Java 21, Spring Boot, Spring Security, JWT |
| Frontend    | Angular                                  |
| Base de données | PostgreSQL, Flyway (migrations)     |
| DevOps      | Docker, Docker Compose, GitHub Actions   |
| Qualité     | JUnit 5, Testcontainers, Jacoco, ESLint  |
| Doc API     | OpenAPI / Swagger UI                     |

## Structure du repo

```
shop/
├── backend/    # API Spring Boot (monolithe modulaire)
├── frontend/   # Application Angular
├── docs/       # Décisions d'architecture, modèle de domaine, roadmap
└── docker/     # docker-compose, fichiers d'infra locale
```

## Démarrage rapide

Prérequis : **JDK 21** (exactement — voir `docs/ARCHITECTURE.md` §0),
**Node 22+**, Docker Desktop.

```bash
# 1. Base de données locale
cd docker && docker compose up -d

# 2. Backend (terminal séparé)
cd backend && mvn spring-boot:run

# 3. Frontend (terminal séparé)
cd frontend && npm install && npx ng serve
```

- API : http://localhost:8080 (Swagger UI : `/swagger-ui.html`)
- Frontend : http://localhost:4200
- Compte admin de démo créé automatiquement : identifiant `admin` (ou
  `admin@shop.local`) / `ChangeMe123!` (dev uniquement — voir `SEED_ADMIN_ENABLED`).
  L'admin choisit ensuite son propre nom d'utilisateur et mot de passe dans
  **Admin → Account** ; le seeder ne les écrase jamais.

### Paiement par carte (Stripe)

Le checkout redirige vers la page de paiement hébergée par Stripe. Il faut
une clé secrète **de test** (Dashboard Stripe → Developers → API keys,
`sk_test_...`), passée au backend par variable d'environnement
`STRIPE_SECRET_KEY` ou dans `backend/application-local.yml` (ignoré par
git) — jamais dans le code :

```yaml
# backend/application-local.yml
app:
  stripe:
    secret-key: sk_test_...
```

Sans clé, le checkout répond 503. Pour payer en test : carte
`4242 4242 4242 4242`, date d'expiration future, CVC quelconque
(`4000 0025 0000 3155` déclenche la validation 3-D Secure).

Pour tester la pile complète conteneurisée (proche prod), voir
[docker/README.md](docker/README.md).

## Documentation

- [Architecture](docs/ARCHITECTURE.md) — décisions techniques et découpage backend
- [Modèle de domaine](docs/DOMAIN_MODEL.md) — entités, relations, MVP vs V2
- [Roadmap](docs/ROADMAP.md) — étapes de construction du projet
- [docker/README.md](docker/README.md) — dev vs pile complète conteneurisée

## État du projet

✅ **MVP complet** (étapes 0 à 10 de la [roadmap](docs/ROADMAP.md)) : auth JWT
avec refresh token, catalogue avec recherche full-text, panier, checkout,
espace admin complet, DevOps (Docker/CI), observabilité, tests, lint, rate
limiting, cache. Voir la [roadmap](docs/ROADMAP.md) pour le détail de
chaque étape et les compromis assumés.

Reste : déploiement cloud (nécessite un choix de service externe), le
repo n'est pas encore poussé sur GitHub, et les fonctionnalités V2
(wishlist, avis produits, promotions).
