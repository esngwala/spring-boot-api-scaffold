package com.esngwala.spring.boot.scaffold.domain.model;

import com.esngwala.spring.boot.scaffold.domain.model.base.AuditableEntity;
import jakarta.persistence.*;
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
