package com.esngwala.spring.boot.scaffold.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app.rate-limit")
@Validated
public record RateLimitProperties(
        /** Maximum requests allowed within the refill period */
        @Min(1) int capacity,
        /** Number of tokens refilled per period */
        @Min(1) int refillTokens,
        /** Refill period in seconds */
        @Min(1) long refillPeriodSeconds,
        /**
         * CIDRs / IPs of trusted reverse proxies whose X-Forwarded-For header is honoured.
         * Leave empty to always use the direct remote address (safest default).
         * Example: ["10.0.0.0/8", "172.16.0.0/12"]
         */
        List<String> trustedProxies
) {
    public RateLimitProperties {
        trustedProxies = trustedProxies == null ? List.of() : List.copyOf(trustedProxies);
    }
}
