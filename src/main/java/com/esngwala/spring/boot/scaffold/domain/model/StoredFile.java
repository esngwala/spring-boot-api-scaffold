package com.esngwala.spring.boot.scaffold.domain.model;

import com.esngwala.spring.boot.scaffold.domain.model.base.AuditableEntity;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileStatus;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileVisibility;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stored_files")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StoredFile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSizeBytes;

    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus fileStatus;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private LocalDateTime expiresAt;
}
