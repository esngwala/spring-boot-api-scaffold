package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain;

import com.esngwala.spring.boot.scaffold.domain.model.auth.EmailVerificationToken;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    /** Invalidates all pending tokens for a user before issuing a new one. */
    @Modifying
    @Query("UPDATE EmailVerificationToken t SET t.usedAt = CURRENT_TIMESTAMP WHERE t.user = :user AND t.usedAt IS NULL")
    void invalidateAllForUser(User user);

    /** Atomically consumes a valid token so concurrent verification requests cannot both succeed. */
    @Modifying
    @Query("UPDATE EmailVerificationToken t SET t.usedAt = :usedAt WHERE t.id = :id AND t.usedAt IS NULL AND t.expiresAt > :usedAt")
    int consumeIfValid(@Param("id") UUID id, @Param("usedAt") Instant usedAt);

    /** Deletes tokens that are expired or have already been used. */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :cutoff OR t.usedAt IS NOT NULL")
    int deleteExpiredOrUsed(@Param("cutoff") Instant cutoff);
}
