package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.permission.UserReadDTO;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Permission;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Role;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserRepository users;

    @Transactional(readOnly = true)
    public List<UserReadDTO> list() {
        return users.findAll().stream()
                .sorted(Comparator.comparing(User::getEmail))
                .map(this::toReadDTO)
                .toList();
    }

    private UserReadDTO toReadDTO(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).sorted().toList();
        List<String> permissions = java.util.stream.Stream.concat(
                        user.getRoles().stream().flatMap(role -> role.getPermissions().stream()),
                        user.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .sorted()
                .toList();
        return new UserReadDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.isEmailVerified(), user.isEnabled(), roles, permissions, user.getCreatedAt());
    }
}
