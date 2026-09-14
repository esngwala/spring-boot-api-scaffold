package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain;

import com.esngwala.spring.boot.scaffold.domain.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface UserRepository extends JpaRepository<User, UUID> { Optional<User> findByEmail(String email); boolean existsByEmail(String email); }
