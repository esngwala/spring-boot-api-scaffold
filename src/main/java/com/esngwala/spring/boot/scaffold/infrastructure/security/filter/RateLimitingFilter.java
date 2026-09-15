package com.esngwala.spring.boot.scaffold.infrastructure.security.filter;

import com.esngwala.spring.boot.scaffold.infrastructure.security.properties.RateLimitProperties;
import com.esngwala.spring.boot.scaffold.shared.exception.ErrorResponse;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties props;
    private final ObjectMapper objectMapper;
    /** Pre-built set of trusted proxy IPs for O(1) lookup. */
    private final Set<String> trustedProxyIps;

    /** One bucket per client IP. ConcurrentHashMap is safe for concurrent access. */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter(RateLimitProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
        this.trustedProxyIps = Set.copyOf(props.trustedProxies());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String ip = resolveClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(ip, k -> newBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {}", ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("X-RateLimit-Retry-After-Seconds",
                    String.valueOf(props.refillPeriodSeconds()));
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(
                    Instant.now(),
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too Many Requests",
                    "Rate limit exceeded. Please try again later.",
                    request.getRequestURI()
            )));
        }
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(
                props.capacity(),
                Refill.greedy(props.refillTokens(), Duration.ofSeconds(props.refillPeriodSeconds()))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Resolves the real client IP.
     * X-Forwarded-For is only trusted when the direct remote address belongs to
     * the configured {@code app.rate-limit.trusted-proxies} list, preventing
     * header spoofing by clients connecting directly.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (!trustedProxyIps.isEmpty() && trustedProxyIps.contains(remoteAddr)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
        }
        return remoteAddr;
    }
}
