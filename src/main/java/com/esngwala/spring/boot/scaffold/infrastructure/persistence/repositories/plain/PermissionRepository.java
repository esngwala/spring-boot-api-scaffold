package com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain;

import com.esngwala.spring.boot.scaffold.domain.model.auth.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);
}
