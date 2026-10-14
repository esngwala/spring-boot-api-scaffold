package com.esngwala.spring.boot.scaffold.infrastructure.seeding;

import com.esngwala.spring.boot.scaffold.domain.model.auth.Role;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1)
public class RoleSeeder {

    @Bean
    CommandLineRunner seedRoles(RoleRepository roles) {
        return args -> {
            seed(roles, "USER");
            seed(roles, "ADMIN");
        };
    }

    static Role getOrCreate(RoleRepository roles, String name) {
        return roles.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roles.save(role);
        });
    }

    private void seed(RoleRepository roles, String name) {
        getOrCreate(roles, name);
    }
}
