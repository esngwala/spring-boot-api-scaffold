package com.esngwala.spring.boot.scaffold.repositories.plain;
import com.esngwala.spring.boot.scaffold.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface RoleRepository extends JpaRepository<Role, UUID> { Optional<Role> findByName(String name); }
