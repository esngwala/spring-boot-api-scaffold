package com.codewithemncore.sb_ecom.dto.category;

import java.time.LocalDateTime;

public record CategoryReadDTO(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
