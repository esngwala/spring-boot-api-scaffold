package com.esngwala.spring.boot.scaffold.infrastructure.security.config;

import com.esngwala.spring.boot.scaffold.application.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@RequiredArgsConstructor
public class AuditingConfig {

    private final CurrentUserService currentUserService;

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return currentUserService::currentUserId;
    }
}
