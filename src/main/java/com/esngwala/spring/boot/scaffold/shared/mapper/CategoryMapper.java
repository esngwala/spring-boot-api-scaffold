package com.esngwala.spring.boot.scaffold.shared.mapper;

import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryUpdateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryCreateDTO;
import com.esngwala.spring.boot.scaffold.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryCreateDTO dto);
    CategoryReadDTO toDto(Category category);
    List<CategoryReadDTO> toDtoList(List<Category> categories);
    void updateEntityFromDto(CategoryUpdateDTO dto, @MappingTarget Category entity);
}
