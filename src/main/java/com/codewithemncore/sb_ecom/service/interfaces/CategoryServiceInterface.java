package com.codewithemncore.sb_ecom.service.interfaces;

import com.codewithemncore.sb_ecom.dto.category.CategoryCreateDTO;
import com.codewithemncore.sb_ecom.dto.category.CategoryReadDTO;
import com.codewithemncore.sb_ecom.dto.category.CategoryUpdateDTO;
import com.codewithemncore.sb_ecom.dto.common.PageRequestParams;
import org.springframework.data.domain.Page;

public interface CategoryServiceInterface {
    CategoryReadDTO create(CategoryCreateDTO dto);
    CategoryReadDTO update(Long id, CategoryUpdateDTO dto);
    void delete(Long id);
    CategoryReadDTO getById(Long id);
    Page<CategoryReadDTO> getPaged(PageRequestParams params);
}
