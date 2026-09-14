package com.esngwala.spring.boot.scaffold.application.service.interfaces;

import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryCreateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryUpdateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.common.PageRequestParams;
import org.springframework.data.domain.Page;

public interface CategoryServiceInterface {
    CategoryReadDTO create(CategoryCreateDTO dto);
    CategoryReadDTO update(Long id, CategoryUpdateDTO dto);
    void delete(Long id);
    CategoryReadDTO getById(Long id);
    Page<CategoryReadDTO> getPaged(PageRequestParams params);
}
