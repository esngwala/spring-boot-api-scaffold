package com.esngwala.spring.boot.scaffold.api.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

public record RoleCreateDTO(@NotBlank @Pattern(regexp = "[A-Z][A-Z0-9_]{1,49}") String name,
                            @NotEmpty Set<@NotBlank @Pattern(regexp = "[a-z0-9]+:[a-z0-9]+") String> permissions) {}
