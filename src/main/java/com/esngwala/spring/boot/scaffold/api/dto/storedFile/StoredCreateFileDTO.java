package com.esngwala.spring.boot.scaffold.api.dto.storedFile;

import com.esngwala.spring.boot.scaffold.domain.model.enums.FileVisibility;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record StoredCreateFileDTO(
        @NotNull(message = "File is required")
        MultipartFile file,
        FileVisibility visibility,
        String category,
        String metadata
) {
    public StoredCreateFileDTO {
        if(visibility == null)
            visibility = FileVisibility.PRIVATE;
    }
}
