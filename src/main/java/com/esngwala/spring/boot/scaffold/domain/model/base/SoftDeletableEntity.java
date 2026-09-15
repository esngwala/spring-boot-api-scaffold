package com.esngwala.spring.boot.scaffold.domain.model.base;

import com.esngwala.spring.boot.scaffold.domain.model.enums.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit-capable entity with application-level soft deletion.
 * Extend this only when deleted rows must be retained and hidden from normal queries.
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("status <> 'DELETED'")
public abstract class SoftDeletableEntity extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    private LocalDateTime statusChangedAt;

    @JdbcTypeCode(Types.BINARY)
    private UUID statusChangedById;

    public void changeStatus(EntityStatus newStatus, UUID changedById) {
        this.status = newStatus;
        this.statusChangedAt = LocalDateTime.now();
        this.statusChangedById = changedById;
    }
}
