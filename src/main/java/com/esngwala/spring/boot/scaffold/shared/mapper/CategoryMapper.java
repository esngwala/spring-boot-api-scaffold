package com.esngwala.spring.boot.scaffold.shared.mapper;

import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryUpdateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryCreateDTO;
import com.esngwala.spring.boot.scaffold.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusChangedAt", ignore = true)
    @Mapping(target = "statusChangedById", ignore = true)
    Category toEntity(CategoryCreateDTO dto);
    CategoryReadDTO toDto(Category category);
    List<CategoryReadDTO> toDtoList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusChangedAt", ignore = true)
    @Mapping(target = "statusChangedById", ignore = true)
    void updateEntityFromDto(CategoryUpdateDTO dto, @MappingTarget Category entity);
}
