package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.permission.PermissionReadDTO;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Permission;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.PermissionRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionManagementService {
    private final PermissionRepository permissions;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public List<PermissionReadDTO> list() {
        return permissions.findAll().stream()
                .sorted(Comparator.comparing(Permission::getName))
                .map(permission -> new PermissionReadDTO(permission.getId(), permission.getName()))
                .toList();
    }

    @Transactional
    public void grant(UUID userId, String permissionName) {
        User user = findUser(userId);
        Permission permission = findPermission(permissionName);
        user.getPermissions().add(permission);
        users.save(user);
    }

    @Transactional
    public void revoke(UUID userId, String permissionName) {
        User user = findUser(userId);
        Permission permission = findPermission(permissionName);
        user.getPermissions().remove(permission);
        users.save(user);
    }

    private User findUser(UUID userId) {
        return users.findById(userId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found"));
    }

    private Permission findPermission(String permissionName) {
        return permissions.findByName(permissionName).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Permission not found"));
    }
}
