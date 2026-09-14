package com.codewithemncore.sb_ecom.dto.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;

public record PageRequestParams(
        @Min(value = 1, message = "pageNumber must be at least 1")
        int pageNumber,

        @Min(value = 1, message = "pageSize must be at least 1")
        @Max(value = 200, message = "pageSize must not exceed 200")
        int pageSize,

        String searchTerm,
        String sortBy,
        Sort.Direction sortDirection
) {
    public static PageRequestParams of(int pageNumber, int pageSize, String searchTerm) {
        return new PageRequestParams(pageNumber, pageSize, searchTerm, "id", Sort.Direction.ASC);
    }
}
