package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.permission.RoleReadDTO;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Permission;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Role;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.PermissionRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RoleRepository;
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
public class RoleManagementService {
    private final RoleRepository roles;
    private final PermissionRepository permissions;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public List<RoleReadDTO> list() {
        return roles.findAll().stream().sorted(Comparator.comparing(Role::getName)).map(role ->
                new RoleReadDTO(role.getId(), role.getName(), role.getPermissions().stream()
                        .map(Permission::getName).sorted().toList())).toList();
    }

    @Transactional
    public void grantPermission(String roleName, String permissionName) {
        Role role = findRole(roleName);
        role.getPermissions().add(findPermission(permissionName));
        roles.save(role);
    }

    @Transactional
    public void revokePermission(String roleName, String permissionName) {
        Role role = findRole(roleName);
        role.getPermissions().remove(findPermission(permissionName));
        roles.save(role);
    }

    @Transactional
    public void assignRole(UUID userId, String roleName) {
        User user = users.findById(userId).orElseThrow(() -> notFound("User not found"));
        user.getRoles().add(findRole(roleName));
        users.save(user);
    }

    @Transactional
    public void removeRole(UUID userId, String roleName) {
        User user = users.findById(userId).orElseThrow(() -> notFound("User not found"));
        user.getRoles().remove(findRole(roleName));
        users.save(user);
    }

    private Role findRole(String name) {
        return roles.findByName(name).orElseThrow(() -> notFound("Role not found"));
    }

    private Permission findPermission(String name) {
        return permissions.findByName(name).orElseThrow(() -> notFound("Permission not found"));
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
