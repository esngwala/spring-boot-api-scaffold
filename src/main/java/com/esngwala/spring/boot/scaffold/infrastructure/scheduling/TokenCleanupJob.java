package com.esngwala.spring.boot.scaffold.infrastructure.scheduling;

import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.EmailVerificationTokenRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.PasswordResetTokenRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Periodically purges stale token rows to prevent indefinite table growth.
 * <p>
 * Schedule: daily at 02:00 UTC (configurable via app.scheduling.token-cleanup-cron).
 * All three token tables are cleaned in a single transaction per run.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Scheduled(cron = "${app.scheduling.token-cleanup-cron:0 0 2 * * *}")
    @Transactional
    public void purgeStaleTokens() {
        Instant now = Instant.now();

        int refreshDeleted = refreshTokenRepository.deleteExpiredOrRevoked(now);
        int passwordResetDeleted = passwordResetTokenRepository.deleteExpiredOrUsed(now);
        int emailVerificationDeleted = emailVerificationTokenRepository.deleteExpiredOrUsed(now);

        log.info("Token cleanup: removed {} refresh, {} password-reset, {} email-verification rows",
                refreshDeleted, passwordResetDeleted, emailVerificationDeleted);
    }
}
