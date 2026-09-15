package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.infrastructure.config.AppProperties;
import com.esngwala.spring.boot.scaffold.infrastructure.messaging.payload.EmailPayload;
import com.esngwala.spring.boot.scaffold.infrastructure.messaging.publisher.EmailQueueProducers;
import com.esngwala.spring.boot.scaffold.domain.model.auth.EmailVerificationToken;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.EmailVerificationTokenRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailQueueProducers emailProducers;
    private final EmailTemplateService emailTemplateService;
    private final AppProperties appProps;

    private final SecureRandom random = new SecureRandom();

    /**
     * Issues a new verification token for {@code user} and dispatches the
     * verification email via the notification queue.
     * Any previous unused tokens for this user are invalidated first.
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        if (user.isEmailVerified()) return; // nothing to do

        tokenRepository.invalidateAllForUser(user);

        String raw = generateRawToken();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(hash(raw));
        token.setExpiresAt(Instant.now().plus(appProps.emailVerification().tokenTtl()));
        tokenRepository.save(token);

        long expiryHours = appProps.emailVerification().tokenTtl().toHours();
        String html = emailTemplateService.renderVerifyEmail(user.getFirstName(), raw, expiryHours);

        emailProducers.sendEmailNotification(new EmailPayload(
                user.getEmail(),
                null,
                "Verify your email address",
                html
        ));

        log.info("Email verification link dispatched for {}", user.getEmail());
    }

    /**
     * Consumes the verification token and marks the user's email as verified.
     *
     * @throws ResponseStatusException 400 if the token is invalid, already used, or expired
     */
    @Transactional
    public void verify(String rawToken) {
        EmailVerificationToken token = tokenRepository
                .findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid or expired verification token"));

        if (token.getUsedAt() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has already been used");

        if (!token.getExpiresAt().isAfter(Instant.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired");

        User user = token.getUser();
        if (tokenRepository.consumeIfValid(token.getId(), Instant.now()) != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has already been used");
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for user {}", user.getEmail());
    }

    /**
     * Re-sends the verification email for the currently authenticated user.
     * No-op if the email is already verified.
     */
    @Transactional
    public void resend(User user) {
        if (user.isEmailVerified())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address is already verified");
        sendVerificationEmail(user);
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
