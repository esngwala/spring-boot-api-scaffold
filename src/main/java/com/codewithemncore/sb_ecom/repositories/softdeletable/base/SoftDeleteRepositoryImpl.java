package com.codewithemncore.sb_ecom.repositories.softdeletable.base;

import com.codewithemncore.sb_ecom.model.base.AuditableEntity;
import com.codewithemncore.sb_ecom.model.enums.EntityStatus;
import com.codewithemncore.sb_ecom.service.CurrentUserService;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class SoftDeleteRepositoryImpl<T extends AuditableEntity<ID>, ID>
        extends SimpleJpaRepository<T, ID>
        implements SoftDeleteRepository<T, ID>
{
    private final EntityManager entityManager;

    /**
     * Static reference set by Spring once CurrentUserService is available.
     * This is necessary because repository implementations are not Spring-managed beans
     * and cannot use constructor injection directly.
     */
    private static CurrentUserService currentUserService;

    @Autowired
    public void setCurrentUserService(CurrentUserService service) {
        SoftDeleteRepositoryImpl.currentUserService = service;
    }

    public SoftDeleteRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void delete(T entity) {
        entity.changeStatus(EntityStatus.DELETED, currentUserId());
        entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void deleteById(@NonNull ID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    @Transactional
    public void deleteAllById(@NonNull Iterable<? extends ID> ids) {
        ids.forEach(id -> deleteById((ID) id));
    }

    @Override
    @Transactional
    public void deleteAll(@NonNull Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteAll() {
        findAll().forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        findAll().forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteAllInBatch(Iterable<T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        ids.forEach(this::deleteById);
    }

    private UUID currentUserId() {
        return currentUserService != null ? currentUserService.currentUserIdOrNull() : null;
    }
}
