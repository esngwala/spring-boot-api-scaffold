package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.permission.UserReadDTO;
import com.esngwala.spring.boot.scaffold.api.dto.permission.UserCreateDTO;
import com.esngwala.spring.boot.scaffold.api.dto.permission.UserUpdateDTO;
import com.esngwala.spring.boot.scaffold.domain.model.auth.Role;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserReadDTO create(UserCreateDTO request) {
        String email = request.email().trim().toLowerCase(java.util.Locale.ROOT);
        if (users.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        User user = User.builder().firstName(request.firstName().trim()).lastName(request.lastName().trim()).email(email)
                .passwordHash(passwordEncoder.encode(request.password())).build();
        request.roles().stream().map(name -> roles.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + name)))
                .forEach(user.getRoles()::add);
        return toReadDTO(users.save(user));
    }

    @Transactional
    public UserReadDTO update(java.util.UUID id, UserUpdateDTO request) {
        User user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String email = request.email().trim().toLowerCase(java.util.Locale.ROOT);
        users.findByEmail(email).filter(existing -> !existing.getId().equals(id)).ifPresent(existing -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered"); });
        user.setFirstName(request.firstName().trim()); user.setLastName(request.lastName().trim()); user.setEmail(email);
        if (request.password() != null && !request.password().isBlank()) user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.getRoles().clear(); request.roles().stream().map(name -> roles.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + name))).forEach(user.getRoles()::add);
        return toReadDTO(users.save(user));
    }

    @Transactional
    public void delete(java.util.UUID id) {
        if (!users.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        users.deleteById(id);
    }

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



