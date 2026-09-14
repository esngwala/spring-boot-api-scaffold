package com.codewithemncore.sb_ecom.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateDTO(
        @NotBlank(message = "Category name is required")
        @Size(max = 100)
        String name
) {
}
