package com.codewithemncore.sb_ecom.config;

import com.codewithemncore.sb_ecom.repositories.softdeletable.base.SoftDeleteRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.codewithemncore.sb_ecom.repositories.softdeletable",
        repositoryBaseClass = SoftDeleteRepositoryImpl.class
)
public class SoftdeleteConfig {
}
