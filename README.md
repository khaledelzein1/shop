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

## Documentation

- [Architecture](docs/ARCHITECTURE.md) — décisions techniques et découpage backend
- [Modèle de domaine](docs/DOMAIN_MODEL.md) — entités, relations, MVP vs V2
- [Roadmap](docs/ROADMAP.md) — étapes de construction du projet

## État du projet

🚧 En cours de construction — voir la [roadmap](docs/ROADMAP.md) pour l'avancement.
