package com.esngwala.spring.boot.scaffold.api.dto.auth;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
public record UserResponse(UUID id, String firstName, String lastName, String email, boolean emailVerified, Instant createdAt, List<String> permissions) { }
