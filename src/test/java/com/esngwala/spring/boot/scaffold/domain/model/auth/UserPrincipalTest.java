package com.esngwala.spring.boot.scaffold.domain.model.auth;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrincipalTest {
    @Test
    void combinesRoleAndDirectPermissionAuthoritiesWithoutDuplicates() {
        Permission reports = permission("reports:read");
        Permission admin = permission("admin:access");
        Role role = new Role();
        role.setName("FINANCE");
        role.getPermissions().add(reports);

        User user = new User();
        user.setRoles(Set.of(role));
        user.setPermissions(Set.of(reports, admin));

        assertThat(new UserPrincipal(user).getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactlyInAnyOrder("ROLE_FINANCE", "reports:read", "admin:access");
    }

    private Permission permission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permission;
    }
}
