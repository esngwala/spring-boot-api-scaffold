package com.esngwala.spring.boot.scaffold.domain.model.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "email_verification_tokens",
    uniqueConstraints = @UniqueConstraint(name = "uk_evt_token_hash", columnNames = "token_hash")
)
@Getter @Setter @NoArgsConstructor
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    /** Set when the token is consumed so it cannot be reused. */
    private Instant usedAt;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }
}
