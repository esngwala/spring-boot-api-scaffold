package com.esngwala.spring.boot.scaffold.dto.auth;
public record TokenResponse(String accessToken, String refreshToken, String tokenType) { }
