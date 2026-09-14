package com.codewithemncore.sb_ecom.repositories.specification;

import com.codewithemncore.sb_ecom.model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> nameContains(String term){
        if(term == null || term.isBlank())
            return (root, query, cb) -> cb.conjunction();
        String like = "%" + term.toLowerCase() + "%";
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), like);
    }
}
