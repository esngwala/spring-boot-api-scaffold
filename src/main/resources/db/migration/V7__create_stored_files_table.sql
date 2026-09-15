CREATE TABLE stored_files (
    id BINARY(16) PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL UNIQUE,
    content_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    checksum VARCHAR(64),
    visibility VARCHAR(20) NOT NULL,
    file_status VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    metadata TEXT,
    expires_at DATETIME,
    
    -- Audit fields (inherited from AuditableEntity)
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    created_by_id BINARY(16),
    updated_by_id BINARY(16),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    status_changed_at DATETIME,
    status_changed_by_id BINARY(16),
    
    INDEX idx_storage_key (storage_key),
    INDEX idx_file_status (file_status),
    INDEX idx_category (category),
    INDEX idx_expires_at (expires_at),
    INDEX idx_visibility (visibility)
);
