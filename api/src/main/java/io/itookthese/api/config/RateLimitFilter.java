package io.itookthese.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private static final int MAX_REQUESTS = 5;
  private static final long WINDOW_MS = 60_000;

  private final ConcurrentHashMap<String, RateWindow> clients = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!"POST".equalsIgnoreCase(request.getMethod())
        || !request.getRequestURI().equals("/api/v1/contact")) {
      filterChain.doFilter(request, response);
      return;
    }

    String clientIp = getClientIp(request);
    RateWindow window =
        clients.compute(
            clientIp,
            (key, existing) -> {
              long now = System.currentTimeMillis();
              if (existing == null || now - existing.windowStart > WINDOW_MS) {
                return new RateWindow(now, new AtomicInteger(1));
              }
              existing.count.incrementAndGet();
              return existing;
            });

    if (window.count.get() > MAX_REQUESTS) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Too many requests. Please try again later.");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String getClientIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private static class RateWindow {
    final long windowStart;
    final AtomicInteger count;

    RateWindow(long windowStart, AtomicInteger count) {
      this.windowStart = windowStart;
      this.count = count;
    }
  }
}
