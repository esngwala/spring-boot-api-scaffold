package com.esngwala.spring.boot.scaffold.api.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateDTO(
        @NotBlank(message = "Category name is required")
        @Size(max = 100)
        String name
) {}
