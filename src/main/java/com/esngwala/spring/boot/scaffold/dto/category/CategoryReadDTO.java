package com.esngwala.spring.boot.scaffold.dto.category;

import java.time.LocalDateTime;

public record CategoryReadDTO(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
