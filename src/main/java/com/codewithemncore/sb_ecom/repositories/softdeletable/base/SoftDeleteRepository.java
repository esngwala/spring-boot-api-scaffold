package com.codewithemncore.sb_ecom.repositories.softdeletable.base;

import com.codewithemncore.sb_ecom.model.base.AuditableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T extends AuditableEntity<ID>, ID> extends JpaRepository<T, ID> {
}
