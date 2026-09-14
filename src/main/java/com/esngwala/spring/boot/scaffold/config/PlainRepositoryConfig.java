package com.esngwala.spring.boot.scaffold.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.esngwala.spring.boot.scaffold.repositories.plain"
        // no repositoryBaseClass — uses the default SimpleJpaRepository
)
public class PlainRepositoryConfig {
}
