package com.esngwala.spring.boot.scaffold.infrastructure.persistence.config;

import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.softdeletable.base.SoftDeleteRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.softdeletable",
        repositoryBaseClass = SoftDeleteRepositoryImpl.class
)
public class SoftdeleteConfig {
}
