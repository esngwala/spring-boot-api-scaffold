package com.esngwala.spring.boot.scaffold.api.dto.auth;
public record TokenResponse(String accessToken, String refreshToken, String tokenType) { }
