-- Fix audit columns: change Long-based *_by_id columns to BINARY(16) UUID,
-- fix status column size, and add an index on status for soft-delete filter performance.

ALTER TABLE categories
    MODIFY COLUMN status         VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    MODIFY COLUMN created_by_id  BINARY(16),
    MODIFY COLUMN updated_by_id  BINARY(16),
    MODIFY COLUMN status_changed_by_id BINARY(16);

-- Index for @SQLRestriction("status <> 'DELETED'") — filters every query
CREATE INDEX idx_categories_status ON categories (status);
