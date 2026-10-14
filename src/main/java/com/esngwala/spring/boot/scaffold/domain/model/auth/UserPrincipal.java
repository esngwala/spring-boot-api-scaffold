package com.esngwala.spring.boot.scaffold.domain.model.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Objects;

public record UserPrincipal(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.stream.Stream.of(
                        user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())),
                        user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(permission -> new SimpleGrantedAuthority(permission.getName())),
                        user.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getName())))
                .flatMap(stream -> stream)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
