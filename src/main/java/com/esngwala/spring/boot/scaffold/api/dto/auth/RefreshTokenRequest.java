package com.esngwala.spring.boot.scaffold.api.dto.auth;
import jakarta.validation.constraints.NotBlank;
public record RefreshTokenRequest(@NotBlank String refreshToken) { }
