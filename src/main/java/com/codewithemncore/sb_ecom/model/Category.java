package com.codewithemncore.sb_ecom.model;

import com.codewithemncore.sb_ecom.model.base.AuditableEntity;
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
