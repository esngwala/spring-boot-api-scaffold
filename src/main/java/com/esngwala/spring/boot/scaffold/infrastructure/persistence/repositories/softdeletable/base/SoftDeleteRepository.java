package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.softdeletable.base;

import com.esngwala.spring.boot.scaffold.domain.model.base.AuditableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T extends AuditableEntity<ID>, ID> extends JpaRepository<T, ID> {
}
