-- Initial schema for the reusable scaffold.
-- This baseline intentionally represents the current model, not its development history.

CREATE TABLE users (
    id BINARY(16) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE roles (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE refresh_tokens (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6),
    expires_at DATETIME(6) NOT NULL,
    session_expires_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_refresh_tokens_user_id (user_id)
);

CREATE TABLE password_reset_tokens (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used_at DATETIME(6),
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uk_prt_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_password_reset_tokens_user_id (user_id)
);

CREATE TABLE email_verification_tokens (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used_at DATETIME(6),
    CONSTRAINT pk_email_verification_tokens PRIMARY KEY (id),
    CONSTRAINT uk_evt_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_evt_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_evt_user_id (user_id)
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    created_by_id BINARY(16),
    updated_by_id BINARY(16),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    status_changed_at DATETIME(6),
    status_changed_by_id BINARY(16),
    CONSTRAINT pk_categories PRIMARY KEY (id),
    INDEX idx_categories_status (status)
);

CREATE TABLE stored_files (
    id BINARY(16) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    checksum VARCHAR(64),
    visibility VARCHAR(20) NOT NULL,
    file_status VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    metadata TEXT,
    expires_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    created_by_id BINARY(16),
    updated_by_id BINARY(16),
    CONSTRAINT pk_stored_files PRIMARY KEY (id),
    CONSTRAINT uk_stored_files_storage_key UNIQUE (storage_key),
    INDEX idx_stored_files_file_status (file_status),
    INDEX idx_stored_files_category (category),
    INDEX idx_stored_files_expires_at (expires_at),
    INDEX idx_stored_files_visibility (visibility)
);
