# Architecture Notes

## Base entity hierarchy
- BaseEntity<ID>: generic id only; concrete entities choose their identifier
  type (for example `Long` or `UUID`)
- AuditableEntity<ID>: audit fields (createdAt/By, updatedAt/By) + status enum
  (ACTIVE/DISABLED/FROZEN/DELETED) replacing a separate soft-delete boolean
- Why: status enum avoids invalid boolean combinations, models real business
  states (frozen/disabled) beyond just deleted
- The generic type makes repository and model ID types explicit. JPA does not
  select a generator from that generic type: the inherited base uses identity
  generation for numeric entities, while UUID entities declare
  `GenerationType.UUID` directly and use a matching column type such as
  `BINARY(16)`.

## Soft delete
- @SQLRestriction("status <> 'DELETED'") on AuditableEntity — applies to
  EVERY query Hibernate generates, unconditionally
- No restore capability by design
- delete()/deleteById()/deleteAll()/batch variants overridden in
  SoftDeleteRepositoryImpl to call entity.changeStatus(DELETED, ...)
  instead of a real DELETE
- Gotcha: @SQLRestriction can't be selectively bypassed per-query — an
  admin "show deleted" view would need Hibernate @Filter, not Specification

## Repository wiring
- Two @EnableJpaRepositories scopes (repositories.softdeletable vs
  repositories.plain), each its own @Configuration class — must be
  non-overlapping packages, @EnableJpaRepositories is not repeatable
- Constructor signature for custom repositoryBaseClass depends on Spring
  Data version — this project uses (JpaEntityInformation, EntityManager)

## MapStruct + Lombok
- Requires lombok-mapstruct-binding in annotationProcessorPaths or
  MapStruct silently generates null for Lombok-sourced getters
- unmappedTargetPolicy = ReportingPolicy.ERROR — fail build on unmapped
  fields instead of silent null

## Orphaned-data / dependent-entity strategy
- Restrict pattern: check for active dependents before delete
  (see InvalidOperationException usage in CategoryService.delete)
- Applies to status-change methods too (freeze/disable), not just delete
