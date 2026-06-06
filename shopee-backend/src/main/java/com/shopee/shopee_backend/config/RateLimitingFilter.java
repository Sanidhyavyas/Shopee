package com.shopee.shopee_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet filter that enforces two tiers of rate limiting using Bucket4j:
 *
 * <ul>
 *   <li><strong>Auth tier</strong> — applied per client IP on
 *       {@code /auth/login} and {@code /auth/forgot-password}.
 *       Default: 5 requests / 1 minute.</li>
 *   <li><strong>API tier</strong> — applied per authenticated userId on
 *       all other {@code /auth/**} and any other protected path.
 *       Default: 100 requests / 1 minute.</li>
 * </ul>
 *
 * Buckets are held in an in-memory {@link ConcurrentHashMap}. This is
 * intentionally simple; replace the map with a Bucket4j distributed
 * backend (Redis, Hazelcast, …) when horizontal scaling is required.
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // ---- Auth-tier limits ----
    @Value("${rate.limit.auth.requests:5}")
    private int authRequests;

    @Value("${rate.limit.auth.window.minutes:1}")
    private int authWindowMinutes;

    // ---- API-tier limits ----
    @Value("${rate.limit.api.requests:100}")
    private int apiRequests;

    @Value("${rate.limit.api.window.minutes:1}")
    private int apiWindowMinutes;

    private final ObjectMapper objectMapper;

    /** Key: clientIp or "user:<userId>" → Bucket */
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        boolean isAuthSensitive = path.equals("/auth/login")
                || path.equals("/auth/forgot-password");

        if (isAuthSensitive) {
            // Per-IP bucket for sensitive auth endpoints
            String ip = resolveClientIp(request);
            Bucket bucket = buckets.computeIfAbsent(
                    "ip:" + ip, k -> buildBucket(authRequests, authWindowMinutes));
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (!probe.isConsumed()) {
                rejectTooManyRequests(response, probe.getNanosToWaitForRefill());
                return;
            }
        } else {
            // Per-userId bucket for all other endpoints (only when a user is authenticated)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                String key = "user:" + auth.getName();
                Bucket bucket = buckets.computeIfAbsent(
                        key, k -> buildBucket(apiRequests, apiWindowMinutes));
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                if (!probe.isConsumed()) {
                    rejectTooManyRequests(response, probe.getNanosToWaitForRefill());
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    // ---- helpers ----

    private Bucket buildBucket(int requests, int windowMinutes) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(requests)
                .refillGreedy(requests, Duration.ofMinutes(windowMinutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void rejectTooManyRequests(HttpServletResponse response,
                                        long nanosToWaitForRefill) throws IOException {
        long retryAfterSeconds = (nanosToWaitForRefill / 1_000_000_000L) + 1;
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                "message", "Too many requests. Please retry after "
                           + retryAfterSeconds + " second(s).");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
