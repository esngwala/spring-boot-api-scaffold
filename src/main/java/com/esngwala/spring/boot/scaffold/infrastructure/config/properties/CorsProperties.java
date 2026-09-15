package com.esngwala.spring.boot.scaffold.infrastructure.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
@Validated
public record CorsProperties(
        /* Allowed origins, e.g. http://localhost:3000 */
        @NotEmpty List<String> allowedOrigins,
        /* Allowed HTTP methods */
        @NotEmpty List<String> allowedMethods,
        /* Allowed request headers */
        @NotEmpty List<String> allowedHeaders,
        /* Headers exposed to the browser */
        @NotNull List<String> exposedHeaders,
        /* Whether to allow credentials (cookies, auth headers) */
        boolean allowCredentials,
        /* Pre-flight cache duration in seconds */
        long maxAge
) {}
