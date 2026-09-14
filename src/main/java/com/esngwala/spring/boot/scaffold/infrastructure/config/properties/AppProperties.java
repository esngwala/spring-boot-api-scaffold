package com.esngwala.spring.boot.scaffold.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(
        @NotBlank String name,
        @NotBlank String supportEmail,
        @NotBlank String baseUrl,
        @NotNull PasswordReset passwordReset,
        @NotNull EmailVerification emailVerification
) {
    public record PasswordReset(
            @NotNull Duration tokenTtl,
            @NotBlank String resetPath
    ) {}

    public record EmailVerification(
            @NotNull Duration tokenTtl,
            @NotBlank String verifyPath
    ) {}
}
