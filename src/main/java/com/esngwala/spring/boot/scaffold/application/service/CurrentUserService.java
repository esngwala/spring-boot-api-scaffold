package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.domain.model.auth.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the UUID of the currently authenticated user from the SecurityContext.
 * Returns empty when no authentication is present (e.g. during system/batch operations).
 */
@Service
public class CurrentUserService {

    public Optional<UUID> currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal.user().getId());
    }

    /** Convenience method — returns null when no user is present, for use in JPA auditing contexts. */
    public UUID currentUserIdOrNull() {
        return currentUserId().orElse(null);
    }
}
