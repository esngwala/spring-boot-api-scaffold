package com.codewithemncore.sb_ecom.model.base;

import com.codewithemncore.sb_ecom.model.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@SQLRestriction("status <> 'DELETED'")
public class AuditableEntity<ID> extends BaseEntity<ID> {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    @JdbcTypeCode(Types.BINARY)
    private UUID createdById;

    @LastModifiedBy
    @JdbcTypeCode(Types.BINARY)
    private UUID updatedById;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
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
