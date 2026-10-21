package com.esngwala.spring.boot.scaffold.api.dto.permission;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UserUpdateDTO(@NotBlank @Size(max = 100) String firstName,
                            @NotBlank @Size(max = 100) String lastName,
                            @NotBlank @Email @Size(max = 254) String email,
                            @Size(min = 8, max = 72) String password,
                            @NotBlank Set<@NotBlank String> roles) {}
