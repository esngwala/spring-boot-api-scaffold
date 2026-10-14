package com.esngwala.spring.boot.scaffold.infrastructure.seeding;

import com.esngwala.spring.boot.scaffold.domain.model.auth.Permission;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Role;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.PermissionRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PermissionSeeder {
    private static final List<String> ALL_PERMISSIONS = List.of(
            "admin:access", "categories:read", "categories:create", "categories:update", "categories:delete",
            "files:read", "files:create", "files:update", "files:delete");

    @Bean
    CommandLineRunner seedPermissions(PermissionRepository permissions, RoleRepository roles) {
        return args -> {
            List<Permission> seeded = ALL_PERMISSIONS.stream()
                    .map(name -> getOrCreate(permissions, name))
                    .toList();
            Role user = RoleSeeder.getOrCreate(roles, "USER");
            Role admin = RoleSeeder.getOrCreate(roles, "ADMIN");
            seeded.stream()
                    .filter(permission -> permission.getName().equals("categories:read") || permission.getName().equals("files:read"))
                    .forEach(user.getPermissions()::add);
            admin.getPermissions().addAll(seeded);
            roles.save(user);
            roles.save(admin);
        };
    }

    static Permission getOrCreate(PermissionRepository permissions, String name) {
        return permissions.findByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            return permissions.save(permission);
        });
    }
}
