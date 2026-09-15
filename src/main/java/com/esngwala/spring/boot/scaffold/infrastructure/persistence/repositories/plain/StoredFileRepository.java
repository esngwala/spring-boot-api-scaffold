package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain;

import com.esngwala.spring.boot.scaffold.domain.model.StoredFile;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {
    List<StoredFile> findByFileStatus(FileStatus status);
    List<StoredFile> findByExpiresAtBefore(LocalDateTime dateTime);
    List<StoredFile> findByCategory(String category);
}
