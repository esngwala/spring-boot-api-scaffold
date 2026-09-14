package com.esngwala.spring.boot.scaffold.model;

import com.esngwala.spring.boot.scaffold.model.base.AuditableEntity;
import jakarta.persistence.Entity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AuditableEntity<Long> {
    private String name;
}
