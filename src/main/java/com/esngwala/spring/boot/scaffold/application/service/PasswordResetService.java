package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.infrastructure.config.AppProperties;
import com.esngwala.spring.boot.scaffold.api.dto.auth.ForgotPasswordRequest;
import com.esngwala.spring.boot.scaffold.api.dto.auth.ResetPasswordRequest;
import com.esngwala.spring.boot.scaffold.infrastructure.messaging.payload.EmailPayload;
import com.esngwala.spring.boot.scaffold.infrastructure.messaging.publisher.EmailQueueProducers;
import com.esngwala.spring.boot.scaffold.domain.model.auth.PasswordResetToken;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.PasswordResetTokenRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailQueueProducers emailProducers;
    private final EmailTemplateService emailTemplateService;
    private final AppProperties appProps;

    private final SecureRandom random = new SecureRandom();

    /**
     * Initiates a password reset: generates a token, persists it, and dispatches
     * a reset email via the password-reset queue.
     *
     * Always returns successfully even when the email is not registered —
     * this prevents user enumeration.
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        userRepository.findByEmail(email).ifPresent(user -> {
            // Invalidate any existing unused tokens for this user first
            tokenRepository.invalidateAllForUser(user);

            String raw = generateRawToken();
            PasswordResetToken token = new PasswordResetToken();
            token.setUser(user);
            token.setTokenHash(hash(raw));
            token.setExpiresAt(Instant.now().plus(appProps.passwordReset().tokenTtl()));
            tokenRepository.save(token);

            long expiryMinutes = appProps.passwordReset().tokenTtl().toMinutes();
            String html = emailTemplateService.renderPasswordReset(
                    user.getFirstName(), user.getEmail(), raw, expiryMinutes);

            emailProducers.sendPasswordResetEmail(new EmailPayload(
                    user.getEmail(),
                    null,
                    "Reset your password",
                    html
            ));

            log.info("Password reset email queued for {}", email);
        });
    }

    /**
     * Validates the reset token and updates the user's password.
     * The token is marked as used after a successful reset.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.password().equals(request.confirmPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");

        PasswordResetToken token = tokenRepository
                .findByTokenHash(hash(request.token()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token"));

        if (token.getUsedAt() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has already been used");

        if (!token.getExpiresAt().isAfter(Instant.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has expired");

        User user = token.getUser();
        if (tokenRepository.consumeIfValid(token.getId(), Instant.now()) != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has already been used");
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        log.info("Password successfully reset for user {}", user.getEmail());
    }

    // --- helpers ---

    private String generateRawToken() {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
