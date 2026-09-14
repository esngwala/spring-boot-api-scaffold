package com.codewithemncore.sb_ecom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.codewithemncore.sb_ecom.repositories.plain"
        // no repositoryBaseClass — uses the default SimpleJpaRepository
)
public class PlainRepositoryConfig {
}
