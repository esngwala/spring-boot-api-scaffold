package com.codewithemncore.sb_ecom.controller;

import com.codewithemncore.sb_ecom.dto.auth.*;
import com.codewithemncore.sb_ecom.model.auth.UserPrincipal;
import com.codewithemncore.sb_ecom.service.AuthService;
import com.codewithemncore.sb_ecom.service.EmailVerificationService;
import com.codewithemncore.sb_ecom.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return authService.current(principal);
    }

    /**
     * Initiates a password reset. Always returns 202 Accepted regardless of
     * whether the email exists, to prevent user enumeration.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Completes the password reset using the token from the reset email.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifies an email address using the token from the verification email.
     * The token is passed as a query parameter so it works directly from email clients.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        emailVerificationService.verify(token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Re-sends the verification email for the authenticated user.
     * Returns 400 if the email is already verified.
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@AuthenticationPrincipal UserPrincipal principal) {
        emailVerificationService.resend(principal.user());
        return ResponseEntity.accepted().build();
    }
}
