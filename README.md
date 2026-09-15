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
docker compose up -d
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Create the local MySQL database `ecom` first. The application runs at
`http://localhost:8080`; Mailpit is available at `http://localhost:8025` and
RabbitMQ management at `http://localhost:15672`.

The `dev` profile creates `admin@localhost` / `admin123`. It exists only for
local development.

## Documentation

- [Architecture](ARCHITECTURE.md): system boundaries, request flow, persistence,
  identity, assets, messaging, and known limits.
- [Help](HELP.md): complete local setup, required configuration, testing, Flyway,
  upload guidance, and troubleshooting.

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
