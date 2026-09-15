package com.esngwala.spring.boot.scaffold.api.dto.storedFile;

import com.esngwala.spring.boot.scaffold.domain.model.enums.FileStatus;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileVisibility;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoredFileReadDTO(
        UUID id,
        String originalFileName,
        String storageKey,
        String contentType,
        Long fileSizeBytes,
        String checksum,
        FileVisibility visibility,
        FileStatus fileStatus,
        String category,
        String metadata,
        String url,
        LocalDateTime createdAt,
        LocalDateTime expiresAt

) {
}
