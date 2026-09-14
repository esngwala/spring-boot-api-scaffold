package com.codewithemncore.sb_ecom.dto.auth;
import java.time.Instant;
import java.util.UUID;
public record UserResponse(UUID id, String firstName, String lastName, String email, boolean emailVerified, Instant createdAt) { }
