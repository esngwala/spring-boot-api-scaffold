package com.esngwala.spring.boot.scaffold.api.controller;

import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryCreateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.category.CategoryUpdateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.common.PageRequestParams;
import com.esngwala.spring.boot.scaffold.application.service.interfaces.CategoryServiceInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryServiceInterface categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryReadDTO> create(@Valid @RequestBody CategoryCreateDTO createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(createRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryReadDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody CategoryUpdateDTO updateBody) {
        return ResponseEntity.ok(categoryService.update(id, updateBody));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryReadDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Paginated list of categories. Replaces the old unbounded getAll().
     * Default: page 1, 20 per page, sorted by id ascending.
     */
    @GetMapping
    public ResponseEntity<Page<CategoryReadDTO>> list(
            @RequestParam(defaultValue = "1")  @Min(1)            int pageNumber,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200)  int pageSize,
            @RequestParam(required = false)                        String searchTerm,
            @RequestParam(defaultValue = "id")                     String sortBy,
            @RequestParam(defaultValue = "ASC")                    Sort.Direction sortDirection) {

        var params = new PageRequestParams(pageNumber, pageSize, searchTerm, sortBy, sortDirection);
        return ResponseEntity.ok(categoryService.getPaged(params));
    }
}
