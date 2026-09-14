package com.esngwala.spring.boot.scaffold.domain.model.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.util.UUID;

@Entity @Table(name = "roles", uniqueConstraints = @UniqueConstraint(name = "uk_roles_name", columnNames = "name"))
@Getter @Setter @NoArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(Types.BINARY) private UUID id;
    @Column(nullable = false, unique = true) private String name;
}
