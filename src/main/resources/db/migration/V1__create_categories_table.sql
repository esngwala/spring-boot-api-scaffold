-- V1__create_categories_table.sql

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    created_by_id BIGINT,
    updated_by_id BIGINT,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    status_changed_at DATETIME(6),
    status_changed_by_id BIGINT,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);