package com.homycarev1.ecommerce.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Small per-instance guard for the two unauthenticated endpoints most likely to
 * be abused. A distributed rate limiter should be used when running multiple
 * API instances, but this still protects a single Railway instance by default.
 */
@Component
public class PublicEndpointRateLimitFilter extends OncePerRequestFilter {
    private static final long WINDOW_SECONDS = 60;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String endpoint = switch (request.getRequestURI()) {
            case "/api/contact" -> "contact";
            case "/api/account/me" -> "login";
            default -> null;
        };
        if (endpoint == null) {
            filterChain.doFilter(request, response);
            return;
        }

        int limit = endpoint.equals("contact") ? 5 : 20;
        long now = Instant.now().getEpochSecond();
        if (windows.size() > 10_000) {
            windows.entrySet().removeIf(entry -> now - entry.getValue().startedAt >= WINDOW_SECONDS);
        }
        String key = endpoint + ":" + clientAddress(request);
        Window current = windows.compute(key, (ignored, previous) -> {
            if (previous == null || now - previous.startedAt >= WINDOW_SECONDS) {
                return new Window(now, 1);
            }
            return new Window(previous.startedAt, previous.count + 1);
        });

        if (current.count > limit) {
            response.setStatus(429);
            response.setHeader("Retry-After", Long.toString(Math.max(1, WINDOW_SECONDS - (now - current.startedAt))));
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests. Please try again shortly.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String clientAddress(HttpServletRequest request) {
        String cloudflareAddress = request.getHeader("CF-Connecting-IP");
        return cloudflareAddress == null || cloudflareAddress.isBlank()
                ? request.getRemoteAddr() : cloudflareAddress;
    }

    private record Window(long startedAt, int count) { }
}
