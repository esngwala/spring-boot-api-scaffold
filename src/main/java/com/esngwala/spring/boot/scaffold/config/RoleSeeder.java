package com.esngwala.spring.boot.scaffold.config;
import com.esngwala.spring.boot.scaffold.model.auth.Role;
import com.esngwala.spring.boot.scaffold.repositories.plain.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RoleSeeder {
    @Bean CommandLineRunner seedRoles(RoleRepository roles) { return args -> { seed(roles, "USER"); seed(roles, "ADMIN"); }; }
    private void seed(RoleRepository roles, String name) { if (roles.findByName(name).isEmpty()) { Role role = new Role(); role.setName(name); roles.save(role); } }
}
