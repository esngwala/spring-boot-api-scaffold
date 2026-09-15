package com.esngwala.spring.boot.scaffold.application.service;
import com.esngwala.spring.boot.scaffold.domain.model.auth.RefreshToken;
import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service @RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    @Value("${security.auth.refresh-token-ttl}") private Duration refreshTokenTtl;
    private final SecureRandom random = new SecureRandom();
    @Transactional public String create(User user) { return create(user, Instant.now().plus(refreshTokenTtl)); }
    @Transactional public String create(User user, Instant sessionExpiresAt) { byte[] bytes = new byte[64]; random.nextBytes(bytes); String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); RefreshToken token = new RefreshToken(); token.setUser(user); token.setTokenHash(hash(raw)); token.setExpiresAt(Instant.now().plus(refreshTokenTtl)); token.setSessionExpiresAt(sessionExpiresAt); repository.save(token); return raw; }
    @Transactional public RefreshToken valid(String raw) { RefreshToken token = repository.findByTokenHash(hash(raw)).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")); if (token.getRevokedAt() != null) { revokeAll(token.getUser()); throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token reuse detected"); } if (!token.getExpiresAt().isAfter(Instant.now()) || !token.getSessionExpiresAt().isAfter(Instant.now())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired"); return token; }
    public void revoke(RefreshToken token) { token.setRevokedAt(Instant.now()); }
    /** Claims a refresh token exactly once, even when two requests race. */
    public boolean revokeIfActive(RefreshToken token) { return repository.revokeIfActive(token.getId(), Instant.now()) == 1; }
    public void revokeIfPresent(String raw) { repository.findByTokenHash(hash(raw)).ifPresent(this::revoke); }
    public void revokeAllActiveTokens(User user) { revokeAll(user); }
    private void revokeAll(User user) { repository.findAllByUserAndRevokedAtIsNull(user).forEach(this::revoke); }
    private String hash(String raw) { try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8))); } catch (Exception ex) { throw new IllegalStateException("SHA-256 unavailable", ex); } }
}
