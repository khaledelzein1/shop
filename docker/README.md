# Docker

Deux fichiers, deux usages différents :

## `docker-compose.yml` — développement au quotidien

Lance uniquement PostgreSQL. Le backend tourne avec `mvn spring-boot:run`
et le frontend avec `ng serve`, tous deux en hot-reload.

```bash
docker compose up -d
# puis, dans deux terminaux séparés :
cd ../backend && mvn spring-boot:run
cd ../frontend && npx ng serve
```

## `docker-compose.prod.yml` — pile complète conteneurisée

Lance PostgreSQL + backend + frontend (via nginx), chacun dans son
conteneur — utile pour tester un environnement proche de la prod en local,
et sert de base au déploiement cloud (étape DevOps).

```bash
cp .env.example .env   # puis éditer .env (JWT_SECRET obligatoire)
docker compose -f docker-compose.prod.yml up --build -d
```

- Frontend : http://localhost:4200 (nginx sert le build Angular et
  reverse-proxy `/api` vers le backend)
- Backend : http://localhost:8080

Ne jamais réutiliser les secrets de `.env.example` tels quels — ce sont
des valeurs placeholder.
