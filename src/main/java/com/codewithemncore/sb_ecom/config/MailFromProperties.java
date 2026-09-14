package com.codewithemncore.sb_ecom.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "mail.from")
@Validated
public record MailFromProperties(
        @NotBlank String address,
        @NotBlank String name
) {}
