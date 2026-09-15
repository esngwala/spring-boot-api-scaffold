# Architecture

This repository is a modular Spring Boot monolith scaffold. It provides shared
infrastructure and a few reference modules, not a completed e-commerce,
payments, crowdfunding, or course application.

## System shape

```text
Client
  │ HTTP / JSON / multipart
  ▼
API controllers
  │ validation + HTTP status + method authorization
  ▼
Application services
  │ transactions + use-case rules
  ├───────────────┬───────────────────┐
  ▼               ▼                   ▼
JPA repositories  Asset storage        RabbitMQ email work
  │               │                   │
  ▼               ▼                   ▼
MySQL + Flyway     local uploads/      SMTP provider
```

The packages currently follow technical layers (`api`, `application`, `domain`,
`infrastructure`, `shared`). As a project grows, group new code by business
module inside those layers or migrate the module to a vertical package. Avoid
letting one module read or modify another module's entities directly.

## Existing modules

| Module | Responsibility |
| --- | --- |
| Identity | Registration, login, JWT access tokens, refresh-token rotation, roles, password reset, email verification. |
| Categories | Small reference CRUD module with paging, allow-listed sorting, auditing, and soft deletion. |
| Assets | Independent file metadata and local byte storage. Business modules own relationships such as product images or course materials. |
| Notifications | RabbitMQ-backed application email delivery with retry, dead-lettering, and SMTP delivery. |

## Request lifecycle

1. Spring Security applies rate limiting, parses an optional bearer token, and
   enforces route/method authorization.
2. A controller validates and translates the request into a DTO.
3. An application service runs the use case inside a transaction where needed.
4. Repositories persist entities through JPA; Flyway owns schema evolution.
5. The controller returns a DTO or streamed asset, never a JPA entity.

## Identity and security

Access tokens are short-lived JWTs signed from `security.jwt.secret`. Refresh
tokens, password-reset tokens, and verification tokens are random opaque values;
only their SHA-256 hashes are stored.

Refresh rotation atomically claims the old refresh token. Password-reset and
verification flows atomically consume tokens so competing requests cannot both
succeed. Roles are expressed as Spring Security authorities (`ROLE_USER`,
`ROLE_ADMIN`).

The security configuration also sets stateless sessions, CORS, CSP, HSTS,
frame denial, MIME-sniffing protection, JSON auth errors, and IP-based rate
limits. Trusted proxy addresses must be configured before relying on
`X-Forwarded-For` in production.

## Persistence model

Each entity declares its own identifier and generator. There is deliberately no
generic ID base class.

```text
AuditableEntity
  └─ SoftDeletableEntity
       └─ Category

AuditableEntity
  └─ StoredFile
```

- `AuditableEntity` provides audit fields only.
- `SoftDeletableEntity` adds `status` and hides `DELETED` rows with
  `@SQLRestriction`.
- `StoredFile` has a separate file lifecycle (`PENDING`, `ACTIVE`, `FAILED`,
  `ARCHIVED`, `DELETED`) and is not generically soft-deleted.
- Use `@SuperBuilder` on each class in an entity inheritance chain when builders
  are desired.
- Flyway migrations are forward-only and are the database schema source of truth.

## Asset module

`StoredFile` owns bytes, internal storage key, coarse visibility, lifecycle, and
metadata. It does not know whether a file belongs to a product, course,
assignment, order, or another business resource.

```text
ProductImage ─────┐
CourseMaterial ───┼──> StoredFile
AssignmentFile ───┘
```

The owning business module must authorize access. The generic asset endpoint
serves only `ACTIVE` public assets. Business services can read an active asset
after their own authorization decision. Storage keys never leave the API.

Upload validates size, metadata, category format, and path containment. Local
writes use a temporary file followed by a move; a failed database transaction
triggers best-effort byte cleanup. `uploads/` is local development state and is
ignored by Git.

## Messaging and email

Email producers publish to durable RabbitMQ queues. Consumers send HTML email
through SMTP. Failed delivery is retried three times with backoff and then sent
to the email dead-letter queue.

This is suitable for the current notification use cases. It is not an outbox
implementation: future critical cross-system workflows need a transactional
outbox and idempotent consumers.

## Configuration and environments

- `application.yml` contains shared settings and required production-style
  environment placeholders.
- `application-dev.yml` supplies local MySQL, RabbitMQ, Mailpit, CORS, and a
  development-only JWT secret.
- `application-prod.yml` tightens rate limits and requires environment values.

Run with `dev` locally. Never use the development JWT secret or seeded admin
credentials in a deployed environment.

## Deliberate current limits

- Local disk storage is for development/single-node deployments; replace it with
  an object-storage provider or shared volume for multiple application nodes.
- File MIME type comes from the client. Add content inspection/type policy when a
  consuming application needs it.
- `StoredFile.checksum` is reserved but is not currently calculated.
- The current test suite is a context smoke test. Add focused service,
  authorization, and concurrency tests as modules are introduced.
