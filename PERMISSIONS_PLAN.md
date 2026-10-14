# Permissions implementation plan

This document is the planning baseline for adding fine-grained permissions to the reusable Spring scaffold and then applying the same design to `D:\proj\crowd-funding\crowd-funding-api`.

## Current baseline

The scaffold currently provides:

- `User` with an eager many-to-many relationship to `Role`.
- Seeded `USER` and `ADMIN` roles and a development admin user.
- JWT authentication that reloads the user and derives `ROLE_USER` or `ROLE_ADMIN` authorities.
- `@EnableMethodSecurity` and controller-level `@PreAuthorize("hasRole('ADMIN')")` checks.
- A token-only login response and `/api/v1/auth/me` identity response.
- A single committed Flyway baseline, `V1__initial_scaffold.sql`.

It does not currently have permissions, role-permission assignments, permission authorities, permission data in `/auth/me`, or permission seeders.

## Recommended model

Use stable string permission keys in a normalized relational model:

```text
users ──< user_roles >── roles ──< role_permissions >── permissions
```

Permission keys should use the `resource:action` form, for example `categories:read`, `categories:create`, and `admin:access`. Roles remain the coarse grouping used by existing API consumers; permissions become the fine-grained authority used by new endpoints and clients.

The security principal should emit both forms:

- `ROLE_ADMIN` and `ROLE_USER` for existing role checks.
- Permission keys such as `admin:access` for `hasAuthority(...)` and `@PreAuthorize("hasAuthority('...')")`.

Permission keys are application constants or typed metadata in code. Database rows are seed data and assignment data, not arbitrary user input. Unknown permissions must fail closed.

## Security and API decisions

- Keep authorization authoritative in Spring Security and service boundaries. UI visibility is only a convenience for clients.
- Keep login token-only. Return permissions from `/api/v1/auth/me` so the current identity is the single source of truth after token issuance and refresh.
- Do not put mutable permission data in long-lived JWT claims. The JWT identifies the user; the request filter reloads current roles and permissions.
- Return a `permissions` array in `UserResponse`, preserving the existing identity fields and keeping role names available only where needed by backend authorization.
- Use `hasAuthority('resource:action')` for fine-grained checks and retain `hasRole('ADMIN')` during migration for existing scaffold endpoints.
- Deny access when the user, role, or permission is disabled or missing. The initial model can use active rows only if lifecycle columns are not yet needed.

## Dependency-ordered tasks

### SP-001 — Baseline and contract plan

This document records the current auth structure, permission vocabulary, API behavior, migration numbering, seed data, and downstream synchronization rules.

### SP-002 — Permission domain and Flyway migration

Add `Permission`, the role-permission relationship, repositories, and a forward-only migration. The scaffold should use `V2__add_permissions.sql` because `V1__initial_scaffold.sql` is already committed. The migration must create named constraints and seed the canonical permission rows. Role assignments belong in the application seeder because baseline roles are created after Flyway migrations during application startup.

### SP-003 — Authority mapping and current-user contract

Extend `UserPrincipal` to emit role and permission authorities. Extend `UserResponse` with sorted permission keys and cover login/current-user behavior without exposing password or token data.

### SP-004 — Permission enforcement

Introduce permission constants and migrate representative endpoints from role-only checks to explicit permissions. Keep compatibility for existing `ADMIN` role checks until every protected endpoint has an intentional permission assignment.

### SP-005 — Seeders and development defaults

Seed canonical permissions, assign baseline permissions to `USER` and `ADMIN`, and ensure the development admin receives the expected permission set. Seeders must be idempotent and must not overwrite administrator changes in deployed environments.

### SP-006 — Verification and security tests

Add unit tests for authority mapping and permission checks, repository/integration coverage for assignments, controller tests for 401/403 behavior, and migration or context verification. Include tests proving that a role without a permission is denied and that existing role checks remain compatible. The administrative API should list known permissions and grant or revoke a direct user permission, with every management operation requiring `admin:access`.

### SP-007 — Crowdfunding implementation

Apply the same model and contracts to `D:\proj\crowd-funding\crowd-funding-api`. Its current schema already has `V1__initial_scaffold.sql` and `V2__create_campaigner_profiles.sql`; use the next migration number there and map campaign-specific permissions only after the shared scaffold permissions are in place.

## Migration and rollout rules

- Never edit `V1__initial_scaffold.sql` or the crowdfunding project’s existing migrations.
- Use forward-only, idempotent inserts for canonical permissions and role assignments.
- Use stable permission names in lower-case `resource:action` form.
- Deploy schema and seed data before enabling endpoint annotations that require new permissions.
- Keep the scaffold’s canonical permission set small; consuming applications add their own permissions in later migrations.
- Validate both repositories independently after synchronization; package names, migration numbers, and domain endpoints differ.

## Review questions before implementation

1. Is the permission key stable and domain-neutral?
2. Should this endpoint require a permission, a role, ownership, or a combination?
3. Does the current-user response expose only safe identity and permission data?
4. Does the migration preserve existing environments and remain safe to rerun?
5. Does the downstream project need additional campaign permissions beyond the scaffold baseline?
