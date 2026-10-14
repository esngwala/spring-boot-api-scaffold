-- Fine-grained authorization vocabulary and role assignments.

CREATE TABLE permissions (
    id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uk_permissions_name UNIQUE (name)
);

CREATE TABLE role_permissions (
    role_id BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

INSERT INTO permissions (id, name) VALUES
    (UUID_TO_BIN(UUID()), 'admin:access'),
    (UUID_TO_BIN(UUID()), 'categories:read'),
    (UUID_TO_BIN(UUID()), 'categories:create'),
    (UUID_TO_BIN(UUID()), 'categories:update'),
    (UUID_TO_BIN(UUID()), 'categories:delete'),
    (UUID_TO_BIN(UUID()), 'files:read'),
    (UUID_TO_BIN(UUID()), 'files:create'),
    (UUID_TO_BIN(UUID()), 'files:update'),
    (UUID_TO_BIN(UUID()), 'files:delete');

-- Role assignments are applied by the idempotent application seeder after
-- RoleSeeder has created the baseline roles.
