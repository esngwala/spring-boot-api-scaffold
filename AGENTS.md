# AGENTS.md

Spring Boot Scaffold - AI Agent Development Guide

## Setup commands

- Build: `./mvnw clean install`
- Run dev server: `./mvnw spring-boot:run`
- Run with profile: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- Run tests: `./mvnw test`
- Package: `./mvnw package`
- Start MySQL (Docker): `docker-compose up -d mysql`
- Start RabbitMQ (Docker): `docker-compose up -d rabbitmq`

## Architecture

This project follows **Enhanced Layered Architecture**:

```
API Layer (controllers + DTOs)
    ↓
Application Layer (services)
    ↓
Domain Layer (entities + enums)
    ↓
Infrastructure Layer (repositories, storage, security, messaging)
```

**Key principles:**
- No business logic in controllers
- Services contain all business logic
- Never expose entities via REST - use DTOs
- Use MapStruct for entity ↔ DTO conversions
- Soft delete by default for most entities
- Transaction boundaries in service methods

## Code style

### Java conventions
- Java 21 features allowed
- Lombok extensively used (`@RequiredArgsConstructor`, `@Getter`, `@Setter`, `@Slf4j`, `@Builder`)
- Records preferred for DTOs
- Explicit types (no `var`)
- 4 spaces indentation, 120 char line length

### Naming conventions
- Entities: Singular nouns (`User`, `Category`, `StoredFile`) - no `Entity` suffix
- DTOs: Suffixed with purpose (`CategoryCreateDTO`, `CategoryReadDTO`, `CategoryUpdateDTO`)
- Services: Singular + `Service` (`AuthService`, `CategoryService`) - no `Impl` suffix
- Repositories: Entity + `Repository` (`UserRepository`, `CategoryRepository`)
- Controllers: Entity + `Controller` (`AuthController`, `CategoryController`)
- Mappers: Entity + `Mapper` (`CategoryMapper`, `StoredFileMapper`)

### Package structure
```
com.esngwala.spring.boot.scaffold/
├── api/                  # REST controllers + DTOs
├── application/service/  # Business logic
├── domain/model/         # JPA entities + enums
├── infrastructure/       # Repos, security, storage, messaging
└── shared/              # Exceptions, mappers
```

## Entity patterns

### Base entity hierarchy
```java
BaseEntity<ID>              // @Id + getter/setter
    ↓
AuditableEntity<ID>        // + createdAt, updatedAt, createdBy, status (soft delete)
```

### Rules
- Most entities extend `AuditableEntity<Long>` or `AuditableEntity<UUID>`
- Long IDs: Use inherited `@GeneratedValue(strategy = IDENTITY)`
- UUID IDs: Override `@Id` without `@GeneratedValue`, assign manually in service:
  ```java
  @Entity
  public class StoredFile extends AuditableEntity<UUID> {
      @Id
      @JdbcTypeCode(Types.BINARY)
      private UUID id;  // Manually assigned before save
  }
  ```
- Soft delete: `AuditableEntity` has `EntityStatus status` field, use `DELETED` status
- Use `@Getter @Setter` on entities (not `@Data`)
- Binary UUIDs: `@JdbcTypeCode(Types.BINARY)`
- Lazy loading by default: `@ManyToOne(fetch = FetchType.LAZY)`

## Repository patterns

### Two types:

1. **Plain repositories** (`infrastructure/persistence/repositories/plain/`)
   - For entities without soft delete (tokens, auth tables)
   - Extend `JpaRepository<Entity, ID>`

2. **Soft-delete repositories** (`infrastructure/persistence/repositories/softdeletable/`)
   - For entities with soft delete
   - Extend `SoftDeleteRepository<Entity, ID>` (extends JpaRepository)
   - Add `JpaSpecificationExecutor<Entity>` if filtering needed

### Query methods
- Use Spring Data query derivation: `findByEmail`, `existsByEmail`
- Custom queries with `@Query` when needed

## Service layer

### Structure
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    
    @Transactional
    public CategoryReadDTO create(CategoryCreateDTO dto) {
        // Business logic
    }
    
    @Transactional(readOnly = true)
    public CategoryReadDTO getById(Long id) {
        return repository.findById(id)
            .map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
```

### Rules
- Constructor injection via `@RequiredArgsConstructor`
- `@Transactional` for writes, `@Transactional(readOnly = true)` for reads
- Throw `ResponseStatusException` for HTTP errors
- Return DTOs, never entities
- Use mappers for conversions
- Log important operations with context

## Controller & API

### Structure
```java
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryReadDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryReadDTO> create(@Valid @RequestBody CategoryCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
}
```

### Conventions
- API versioning: `/api/v1/...`
- Plural resource names: `/api/v1/categories`, `/api/v1/files`
- HTTP status codes: 200 (GET), 201 (POST), 204 (DELETE), 400 (validation), 401 (auth), 403 (forbidden), 404 (not found)
- `@Valid` for request validation
- Security: `@PreAuthorize("hasRole('ADMIN')")` or `@PreAuthorize("isAuthenticated()")`
- Use `ResponseEntity<T>` for explicit status codes

### DTOs
- Records preferred for immutability
- Request DTOs: `CategoryCreateDTO`, `CategoryUpdateDTO`
- Response DTOs: `CategoryReadDTO`
- Validation annotations: `@NotBlank`, `@NotNull`, `@Valid`

## Security

### Authentication
- JWT-based with refresh token rotation
- Email verification required before login (enforced in `AuthService.login()`)
- Password reset via email token

### Public endpoints
```
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
POST /api/v1/auth/resend-verification-email
POST /api/v1/auth/verify-email
POST /api/v1/auth/refresh-token
```

### Protected endpoints
- Default: Require authentication
- Admin only: Add `@PreAuthorize("hasRole('ADMIN')")`

### Token security
- JWT for API authentication
- Refresh tokens stored in DB with expiry
- All verification/reset tokens hashed before storage
- BCrypt for passwords

## File storage

### Architecture
- **StoredFile module is decoupled** from business logic
- Business modules store only the file UUID (not full entity)
- Access control handled by business modules, not file storage
- Storage abstraction via `StorageProvider` interface

### File visibility
- `PUBLIC`: Anyone can download (no auth)
- `PRIVATE`: Requires business module to enforce access control

### Usage pattern
1. Upload file: `StoredFileService.uploadFile(dto)` → returns UUID
2. Store UUID in business entity
3. Download file: Business module checks ownership, calls `storedFileService.downloadActiveFile(uuid)`

### Storage key format
```
{category}/{uuid}.{extension}
```

### File status
- `PENDING`: Being uploaded
- `ACTIVE`: Available
- `DELETED`: Soft-deleted

## Email & messaging

### Architecture
- Async email via RabbitMQ
- Thymeleaf templates in `src/main/resources/templates/email/`
- Three queue types: welcome, password-reset, notification

### Email flow
1. Service calls producer: `emailProducers.sendEmailNotification(payload)`
2. Message published to RabbitMQ
3. Consumer sends email asynchronously

### Template variables
- Set by `EmailTemplateService`: `${appName}`, `${supportEmail}`, `${year}`, `${firstName}`, action URLs

### Creating new templates
1. Create HTML in `templates/email/{name}.html`
2. Use Thymeleaf: `th:text="${var}"`
3. Add render method in `EmailTemplateService`
4. Configure queue in `application.yml` if new queue needed

## Database migrations

### Flyway migrations
- Location: `src/main/resources/db/migration/`
- Naming: `V{number}__{description}.sql`
- Example: `V1__create_users_table.sql`

### Rules
- Never modify committed migrations
- Use BINARY(16) for UUID columns
- Use BIGINT AUTO_INCREMENT for Long IDs
- Name all constraints: `CONSTRAINT fk_user_roles_user FOREIGN KEY...`
- Table names: plural, snake_case (`users`, `stored_files`)
- Column names: snake_case (`created_at`, `first_name`)

## Configuration

### Files
- `application.yml`: Base config (uses env vars with defaults)
- `application-dev.yml`: Dev overrides (optional)
- `application-prod.yml`: Prod overrides (optional)

### Environment variables
Always provide defaults to avoid `${VAR}` in output:
```yaml
app:
  name: ${APP_NAME:Spring Scaffold}
  base-url: ${APP_BASE_URL:http://localhost:8080}
  support-email: ${SUPPORT_EMAIL:support@example.com}
```

### Required environment variables
- Database: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- SMTP: `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`
- JWT: `JWT_SECRET`, `JWT_EXPIRATION`, `JWT_REFRESH_EXPIRATION`
- RabbitMQ: `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`

## Testing

### Commands
- Run all tests: `./mvnw test`
- Run specific test: `./mvnw test -Dtest=CategoryServiceTest`
- Skip tests: `./mvnw install -DskipTests`

### Test strategy (when adding tests)
- Unit tests for services (mock repositories)
- Integration tests for repositories (test DB)
- API tests for controllers (MockMvc)
- No tests for entities/DTOs (just data classes)

## Common patterns

### Creating new features (e.g., "Products")

1. **Entity** (`domain/model/Product.java`)
   - Extend `AuditableEntity<Long>`
   - Use `@Builder`, `@Getter`, `@Setter`

2. **Migration** (`V{N}__create_products_table.sql`)
   - Create table with all columns, constraints, indexes

3. **Repository** (`infrastructure/persistence/repositories/softdeletable/ProductRepository.java`)
   - Extend `SoftDeleteRepository<Product, Long>`
   - Add `JpaSpecificationExecutor<Product>` if needed

4. **DTOs** (`api/dto/product/`)
   - `ProductCreateDTO` (record)
   - `ProductReadDTO` (record)
   - `ProductUpdateDTO` (record, optional)

5. **Mapper** (`shared/mapper/ProductMapper.java`)
   - `@Mapper(componentModel = "spring")`
   - Define `toEntity()` and `toDto()`

6. **Service** (`application/service/ProductService.java`)
   - `@Service`, `@RequiredArgsConstructor`, `@Slf4j`
   - Inject repository and mapper
   - Use `@Transactional`

7. **Controller** (`api/controller/ProductController.java`)
   - `@RestController`, `@RequestMapping("/api/v1/products")`
   - Add security annotations
   - Return `ResponseEntity<DTO>`

## Common mistakes to avoid

❌ **Don't expose entities in controllers**
```java
// BAD
public ResponseEntity<Category> get(@PathVariable Long id) {
    return ResponseEntity.ok(repository.findById(id).get());
}
```

❌ **Don't put business logic in controllers**
```java
// BAD - logic belongs in service
@PostMapping
public ResponseEntity<Category> create(@RequestBody Category category) {
    if (repository.existsByName(category.getName())) {
        throw new RuntimeException("Duplicate");
    }
    return ResponseEntity.ok(repository.save(category));
}
```

❌ **Don't manually assign Long IDs**
```java
// BAD - JPA handles IDENTITY generation
category.setId(123L);
repository.save(category);
```

❌ **Don't forget @Transactional**
```java
// BAD - no transaction
public CategoryReadDTO create(CategoryCreateDTO dto) {
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
}
```

❌ **Don't hard delete auditable entities**
```java
// BAD
repository.delete(entity);

// GOOD
entity.setStatus(EntityStatus.DELETED);
repository.save(entity);
```

❌ **Don't use .get() without null check**
```java
// BAD
Category category = repository.findById(id).get();

// GOOD
Category category = repository.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
```

## Questions before implementing

- Does this entity need soft delete? → Use `AuditableEntity` + `SoftDeleteRepository`
- What ID type? → Long (auto) or UUID (manual)
- What DTOs needed? → CreateDTO, ReadDTO, UpdateDTO (optional)
- Who can access? → Add `@PreAuthorize` annotation
- Async processing needed? → Use RabbitMQ pattern
- Email notification? → Use email template + queue
- File storage? → Follow StoredFile decoupling pattern

## Logging

- Use SLF4J via `@Slf4j`
- `log.info()` - Important operations (user created, file uploaded)
- `log.warn()` - Recoverable errors
- `log.error()` - Exceptions, failures
- `log.debug()` - Detailed debugging
- Include context: `log.info("File uploaded: {}", fileId)`

## Code quality

- IntelliJ default Java formatter
- Max line length: 120 characters
- 4 spaces indentation (no tabs)
- Javadoc on public service methods
- Inline comments only for complex logic
- No commented-out code in commits
- Always log or rethrow exceptions

---

**Project**: Spring Boot Scaffold  
**Version**: 0.0.1-SNAPSHOT  
**Spring Boot**: 4.1.1  
**Java**: 21  
**Build Tool**: Maven
