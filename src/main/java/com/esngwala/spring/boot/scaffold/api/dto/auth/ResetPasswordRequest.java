package com.esngwala.spring.boot.scaffold.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank String confirmPassword
) {}
