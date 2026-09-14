package com.codewithemncore.sb_ecom.repositories.softdeletable;

import com.codewithemncore.sb_ecom.model.Category;
import com.codewithemncore.sb_ecom.repositories.softdeletable.base.SoftDeleteRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends SoftDeleteRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByNameIgnoreCase(String name);
}
