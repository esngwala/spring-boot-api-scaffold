package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain;
import com.esngwala.spring.boot.scaffold.domain.model.auth.RefreshToken;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUserAndRevokedAtIsNull(User user);

    /** Deletes tokens that are either expired or have been revoked, older than the given cutoff. */
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :cutoff OR t.revokedAt IS NOT NULL")
    int deleteExpiredOrRevoked(@Param("cutoff") Instant cutoff);
}
