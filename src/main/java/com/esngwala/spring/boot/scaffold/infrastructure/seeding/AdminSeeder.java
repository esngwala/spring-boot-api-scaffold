package com.esngwala.spring.boot.scaffold.infrastructure.seeding;

import com.esngwala.spring.boot.scaffold.domain.model.auth.Role;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RoleRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Seeds a default admin user for development purposes.
 * Only runs when the 'dev' profile is active.
 */
@Configuration
@Profile("dev")
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Bean
    CommandLineRunner seedAdmin(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String adminEmail = "admin@localhost.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new IllegalStateException("ADMIN role not found. Ensure RoleSeeder runs first."));

                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRoles(Set.of(adminRole));
                admin.setEmailVerified(true);
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);

                userRepository.save(admin);
                log.info("✅ Admin user seeded: {} / admin123", adminEmail);
            } else {
                log.info("ℹ️  Admin user already exists: {}", adminEmail);
            }
        };
    }
}
