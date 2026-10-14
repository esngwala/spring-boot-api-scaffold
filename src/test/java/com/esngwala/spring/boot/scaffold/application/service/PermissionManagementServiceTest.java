package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.domain.model.auth.Permission;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.PermissionRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionManagementServiceTest {
    @Mock PermissionRepository permissions;
    @Mock UserRepository users;
    @InjectMocks PermissionManagementService service;

    @Test
    void grantsAndRevokesDirectPermission() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        Permission permission = permission("reports:read");
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(permissions.findByName("reports:read")).thenReturn(Optional.of(permission));

        service.grant(userId, "reports:read");
        service.revoke(userId, "reports:read");

        verify(users, org.mockito.Mockito.times(2)).save(user);
    }

    private Permission permission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permission;
    }
}
