package com.esngwala.spring.boot.scaffold.application.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final Duration accessTokenTtl;
    public JwtService(@Value("${security.jwt.secret}") String secret, @Value("${security.jwt.access-token-ttl}") Duration ttl) {
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); accessTokenTtl = ttl;
    }
    public String generateToken(String email) { Instant now = Instant.now(); return Jwts.builder().subject(email).issuedAt(Date.from(now)).expiration(Date.from(now.plus(accessTokenTtl))).signWith(signingKey).compact(); }
    public String extractEmail(String token) { return claims(token).getSubject(); }
    public boolean isValid(String token, UserDetails user) { return extractEmail(token).equals(user.getUsername()) && claims(token).getExpiration().after(new Date()); }
    private Claims claims(String token) { return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload(); }
}
