package com.esngwala.spring.boot.scaffold.api.dto.permission;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserReadDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        boolean emailVerified,
        boolean enabled,
        List<String> roles,
        List<String> permissions,
        Instant createdAt) {
}
