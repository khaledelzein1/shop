# Shop — full-stack e-commerce platform

A complete online clothing and shoe store, built as a portfolio project to be
close to a real company codebase: a modular Spring Boot monolith, an Angular
front end, card payments, an admin back office, automated tests and CI.

![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?logo=githubactions&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-22-DD0031?logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Flyway-4169E1?logo=postgresql&logoColor=white)

## Features

**Storefront**
- Catalog by category (T-shirts, jackets, pants, shoes) with full-text search and filters
- Product pages grouped by fit (Short Sleeve, Puffer, Leather…), with color swatches,
  photo galleries per color and a quick size picker
- "Find my size" helper
- Cart, checkout with saved addresses, and order history
- Smooth animations: page transitions, staggered entrances and hover effects
  (disabled automatically for users who prefer reduced motion)

**Payments**
- **Stripe Checkout** (hosted payment page, 3-D Secure) when a Stripe key is configured
- Otherwise a built-in **demo card processor** that runs the same checks (Luhn, expiry,
  CVC) without charging anything, so the project works out of the box
- A scheduled job reconciles payments that were left pending

**Admin back office** (`/admin`)
- **Sales dashboard**: revenue, orders, average order value, items sold, revenue per day,
  best sellers and latest sales, over 7 days to 12 months
- **Products**: create, edit and delete products, variants, stock and photos
- **Categories**, **orders** (status workflow with stock restored on cancellation)
  and **users** (enable / disable accounts)
- **Account**: the admin chooses their own username and password

**Security & engineering**
- JWT access tokens with rotating refresh tokens; login with email or username
- Role-based access (customer / admin), rate limiting on login and sign-up
- Database schema and catalog data versioned with Flyway migrations
- Caching (Caffeine), OpenAPI / Swagger UI, Actuator metrics
- Tests: JUnit 5 + Mockito unit tests, Testcontainers integration tests, front-end unit tests
- Lint & format: Spotless (Google Java Format), ESLint
- CI on every push: back-end build & tests, front-end lint/tests/build, Docker image builds

## Tech stack

| Area      | Technologies                                       |
|-----------|----------------------------------------------------|
| Back end  | Java 21, Spring Boot 3.3, Spring Security, JWT     |
| Front end | Angular 22, TypeScript, SCSS                       |
| Database  | PostgreSQL, Flyway                                 |
| Payments  | Stripe Checkout                                    |
| DevOps    | Docker, Docker Compose, nginx, GitHub Actions      |
| Quality   | JUnit 5, Mockito, Testcontainers, Spotless, ESLint |
| API docs  | OpenAPI / Swagger UI                               |

## Project structure

```
shop/
├── backend/    # Spring Boot API (modular monolith: catalog, cart, order, payment, user, auth…)
├── frontend/   # Angular application (storefront + admin)
├── docker/     # docker-compose files (dev database, full containerized stack)
└── docs/       # Architecture decisions, domain model, roadmap
```

## Getting started

**Prerequisites:** JDK 21 (exactly, see `docs/ARCHITECTURE.md` §0), Node 22+, Docker Desktop.

```bash
# 1. Start the database
cd docker && docker compose up -d

# 2. Start the back end (separate terminal)
cd backend && mvn spring-boot:run

# 3. Start the front end (separate terminal)
cd frontend && npm install && npx ng serve
```

- Website: http://localhost:4200
- API: http://localhost:8080 (Swagger UI at `/swagger-ui.html`)
- Demo admin account, created automatically in development: username `admin`
  (or `admin@shop.local`), password `ChangeMe123!`. Change it from **Admin → Account**.
  The account is only created when `SEED_ADMIN_ENABLED` is true, and existing credentials
  are never overwritten.

### Card payments

Without any configuration, checkout uses the demo card processor. To use Stripe instead,
get a **test** secret key (Stripe Dashboard → Developers → API keys, `sk_test_...`) and pass
it through the `STRIPE_SECRET_KEY` environment variable, or put it in
`backend/application-local.yml` (git-ignored). Never put it in the code:

```yaml
# backend/application-local.yml
app:
  stripe:
    secret-key: sk_test_...
```

Test cards (they behave the same in demo mode and with Stripe):

| Card number           | Result                          |
|-----------------------|---------------------------------|
| `4242 4242 4242 4242` | Payment accepted                |
| `4000 0025 0000 3155` | Asks for 3-D Secure (Stripe)    |
| `4000 0000 0000 0002` | Declined                        |
| `4000 0000 0000 9995` | Declined: insufficient funds    |

Use any future expiry date and any CVC.

### Full containerized stack

To run PostgreSQL, the back end and the front end (served by nginx) in containers,
close to a production setup, see [docker/README.md](docker/README.md).

## Running the tests

```bash
cd backend && mvn verify          # unit + integration tests (Docker required for Testcontainers)
cd frontend && npx ng test        # front-end unit tests
cd frontend && npx eslint src     # lint
```

## Documentation

The design documents are written in French:

- [Architecture](docs/ARCHITECTURE.md): technical decisions and back-end modules
- [Domain model](docs/DOMAIN_MODEL.md): entities, relations, MVP vs V2
- [Roadmap](docs/ROADMAP.md): how the project was built, step by step, and the trade-offs made

## Status

The MVP is complete: authentication, catalog and search, cart, checkout and payments,
admin back office with sales reporting, Docker, CI, observability and tests.

Next steps: cloud deployment, and V2 features (wishlist, product reviews, promotions).
