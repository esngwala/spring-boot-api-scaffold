package com.esngwala.spring.boot.scaffold.service;

import com.esngwala.spring.boot.scaffold.config.AppProperties;
import com.esngwala.spring.boot.scaffold.dto.auth.*;
import com.esngwala.spring.boot.scaffold.dto.auth.*;
import com.esngwala.spring.boot.scaffold.messaging.payload.EmailPayload;
import com.esngwala.spring.boot.scaffold.messaging.publisher.EmailQueueProducers;
import com.esngwala.spring.boot.scaffold.model.auth.Role;
import com.esngwala.spring.boot.scaffold.model.auth.User;
import com.esngwala.spring.boot.scaffold.model.auth.UserPrincipal;
import com.esngwala.spring.boot.scaffold.repositories.plain.RoleRepository;
import com.esngwala.spring.boot.scaffold.repositories.plain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AppProperties.class)
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokens;
    private final EmailQueueProducers emailProducers;
    private final EmailTemplateService emailTemplateService;
    private final EmailVerificationService emailVerificationService;
    private final AppProperties appProps;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (!request.password().equals(request.confirmPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");

        if (users.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");

        Role role = roles.findByName("USER")
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default USER role is not configured"));

        User user = User
                .builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password())).build();
        user.getRoles().add(role);
        User saved = users.save(user);

        // Dispatch welcome email asynchronously via RabbitMQ
        String html = emailTemplateService.renderWelcome(saved.getFirstName());
        emailProducers.sendWelcomeEmail(new EmailPayload(
                saved.getEmail(),
                null,
                "Welcome to " + appProps.name() + "!",
                html
        ));

        // Dispatch email verification link
        emailVerificationService.sendVerificationEmail(saved);

        return response(saved);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        }
        catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        User user = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return new TokenResponse(
                jwtService.generateToken(email),
                refreshTokens.create(user),
                "Bearer");
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        var token = refreshTokens.valid(request.refreshToken());
        refreshTokens.revoke(token);
        User user = token.getUser();
        return new TokenResponse(
                jwtService.generateToken(user.getEmail()),
                refreshTokens.create(user, token.getSessionExpiresAt()),
                "Bearer");
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokens.revokeIfPresent(request.refreshToken());
    }

    @Transactional(readOnly = true)
    public UserResponse current(UserPrincipal principal) {
        return response(principal.user());
    }

    private UserResponse response(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getCreatedAt());
    }
}
