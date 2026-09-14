package com.codewithemncore.sb_ecom.repositories.plain;
import com.codewithemncore.sb_ecom.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface UserRepository extends JpaRepository<User, UUID> { Optional<User> findByEmail(String email); boolean existsByEmail(String email); }
