package com.esngwala.spring.boot.scaffold.api.controller;

import com.esngwala.spring.boot.scaffold.api.dto.permission.PermissionReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.permission.RoleReadDTO;
import com.esngwala.spring.boot.scaffold.application.service.PermissionManagementService;
import com.esngwala.spring.boot.scaffold.application.service.RoleManagementService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class PermissionManagementController {
    private final PermissionManagementService permissionService;
    private final RoleManagementService roleService;

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<List<PermissionReadDTO>> listPermissions() {
        return ResponseEntity.ok(permissionService.list());
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<List<RoleReadDTO>> listRoles() {
        return ResponseEntity.ok(roleService.list());
    }

    @PutMapping("/roles/{roleName}/permissions/{permissionName}")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<Void> grantRolePermission(
            @PathVariable @Pattern(regexp = "[A-Z_]+") String roleName,
            @PathVariable @Pattern(regexp = "[a-z0-9]+:[a-z0-9]+") String permissionName) {
        roleService.grantPermission(roleName, permissionName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/roles/{roleName}/permissions/{permissionName}")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<Void> revokeRolePermission(
            @PathVariable @Pattern(regexp = "[A-Z_]+") String roleName,
            @PathVariable @Pattern(regexp = "[a-z0-9]+:[a-z0-9]+") String permissionName) {
        roleService.revokePermission(roleName, permissionName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId,
            @PathVariable @Pattern(regexp = "[A-Z_]+") String roleName) {
        roleService.assignRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId,
            @PathVariable @Pattern(regexp = "[A-Z_]+") String roleName) {
        roleService.removeRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/permissions/{permissionName}")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<Void> grant(
            @PathVariable UUID userId,
            @PathVariable @Pattern(regexp = "[a-z0-9]+:[a-z0-9]+") String permissionName) {
        permissionService.grant(userId, permissionName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/permissions/{permissionName}")
    @PreAuthorize("hasAuthority(T(com.esngwala.spring.boot.scaffold.domain.model.auth.Permissions).ADMIN_ACCESS)")
    public ResponseEntity<Void> revoke(
            @PathVariable UUID userId,
            @PathVariable @Pattern(regexp = "[a-z0-9]+:[a-z0-9]+") String permissionName) {
        permissionService.revoke(userId, permissionName);
        return ResponseEntity.noContent().build();
    }
}
