package com.esngwala.spring.boot.scaffold.api.dto.category;

import java.time.LocalDateTime;

public record CategoryReadDTO(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
