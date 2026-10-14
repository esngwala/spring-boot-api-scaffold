package com.esngwala.spring.boot.scaffold.api.dto.permission;

import java.util.List;
import java.util.UUID;

public record RoleReadDTO(UUID id, String name, List<String> permissions) {
}
