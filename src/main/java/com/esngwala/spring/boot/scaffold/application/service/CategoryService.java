package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryCreateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryUpdateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.common.PageRequestParams;
import com.esngwala.spring.boot.scaffold.shared.exception.DuplicateResourceException;
import com.esngwala.spring.boot.scaffold.shared.exception.ResourceNotFoundException;
import com.esngwala.spring.boot.scaffold.shared.mapper.CategoryMapper;
import com.esngwala.spring.boot.scaffold.domain.model.Category;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.softdeletable.CategoryRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.specification.CategorySpecification;
import com.esngwala.spring.boot.scaffold.application.service.interfaces.CategoryServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryReadDTO create(CategoryCreateDTO dto) {
        categoryRepository.findByNameIgnoreCase(dto.name())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Category '" + dto.name() + "' already exists");
                });
        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryReadDTO update(Long id, CategoryUpdateDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        categoryMapper.updateEntityFromDto(dto, category);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryReadDTO getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryReadDTO> getPaged(PageRequestParams params) {
        Specification<Category> spec = Specification
                .where(CategorySpecification.nameContains(params.searchTerm()));

        Pageable pageable = PageRequest.of(
                params.pageNumber() - 1,
                params.pageSize(),
                Sort.by(params.sortDirection(), params.sortBy()));

        return categoryRepository.findAll(spec, pageable).map(categoryMapper::toDto);
    }
}
