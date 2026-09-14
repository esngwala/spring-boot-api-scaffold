package com.esngwala.spring.boot.scaffold.application.service;
import com.esngwala.spring.boot.scaffold.domain.model.auth.UserPrincipal;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) {
        return userRepository
                .findByEmail(email).map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
