package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.softdeletable;

import com.esngwala.spring.boot.scaffold.domain.model.Category;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.softdeletable.base.SoftDeleteRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends SoftDeleteRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByNameIgnoreCase(String name);
}
