package com.esngwala.spring.boot.scaffold.repositories.softdeletable;

import com.esngwala.spring.boot.scaffold.model.Category;
import com.esngwala.spring.boot.scaffold.repositories.softdeletable.base.SoftDeleteRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends SoftDeleteRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByNameIgnoreCase(String name);
}
