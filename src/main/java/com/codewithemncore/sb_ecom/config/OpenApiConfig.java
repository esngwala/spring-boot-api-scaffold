package com.codewithemncore.sb_ecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger UI configuration.
 *
 * Active on the "dev" profile only — never loaded in production.
 * UI available at: http://localhost:8080/swagger-ui.html
 * JSON spec at:    http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Monolith Scaffold API")
                        .description("""
                                Industry-grade Spring Boot monolith scaffold.
                                
                                **Authentication**: obtain a JWT via `POST /api/v1/auth/login`,
                                then click **Authorize** and enter `<your-token>` (without the Bearer prefix —
                                the scheme adds it automatically).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("codewithemncore")
                                .url("https://github.com/codewithemncore"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT access token. Obtained from POST /api/v1/auth/login.")));
    }
}
