# Spring Boot Monolith Scaffold

A reusable Spring Boot foundation for building secure modular monoliths. It
includes identity, role-based access, Flyway migrations, a soft-delete reference
module, RabbitMQ-backed email, and an independent asset-storage module.

It is a starting point—not a prebuilt e-commerce, order, payments, course, or
crowdfunding system. Add those business modules on top of the established
boundaries.

## Included

- JWT authentication with refresh-token rotation
- Hashed refresh, password-reset, and email-verification tokens
- Role-based security (`USER`, `ADMIN`), CORS, security headers, and rate limiting
- Flyway-managed MySQL schema with JPA validation
- Auditing and opt-in soft deletion
- Paginated category reference API
- Local asset storage with public/private visibility and business-module access hooks
- RabbitMQ queues, retries, dead-letter routing, and HTML email templates
- Development profile, Docker Compose support for RabbitMQ and Mailpit, and a
  development admin seed

## Quick start

```powershell
docker compose up -d rabbitmq mailpit
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Create the local MySQL database `spring_scaffold` first (or set `DB_URL` to
another local database). The application runs at
`http://localhost:8080`; Mailpit is available at `http://localhost:8025` and
RabbitMQ management at `http://localhost:15672`.

The `dev` profile creates `admin@localhost.com` / `admin123`. It exists only for
local development.

## Documentation

- [Architecture](ARCHITECTURE.md): system boundaries, request flow, persistence,
  identity, assets, messaging, and known limits.
- [Help](HELP.md): complete local setup, required configuration, testing, Flyway,
  upload guidance, and troubleshooting.
- [Permissions implementation plan](PERMISSIONS_PLAN.md): current authorization
  baseline, permission model, migration order, seeders, tests, and downstream
  crowdfunding synchronization.

The repository contains one baseline migration, `V1__initial_scaffold.sql`.
Keep the initial schema consolidated in that file until it has been applied to
a shared or production database; after that, use forward-only numbered
migrations for schema changes.

## Project principles

- Entity identity is an explicit per-entity decision.
- Audit fields, soft deletion, and business lifecycle states are separate concerns.
- Business modules own their relationships and authorization rules.
- Files are independent assets; internal storage keys are never public API values.
- Flyway is the database schema source of truth.
- Start simple, then introduce only the infrastructure a business module needs.

## Build

```powershell
.\mvnw.cmd package -DskipTests
.\mvnw.cmd test
```

See [HELP.md](HELP.md) for production configuration requirements. Never commit
secrets, local upload data, or production connection details.
