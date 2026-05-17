package by.java.enterprise.jwtservice.filter;

import by.java.enterprise.jwtservice.service.JwtService;
import by.java.enterprise.jwtservice.storage.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                try {
                    Claims claims = jwtService.parseToken(token);

                    UUID userId = UUID.fromString(claims.get("id", String.class));
                    String role = claims.get("role", String.class);

                    UserContext.setUserId(userId);
                    UserContext.setUserRole(role);
                } catch (Exception e) {
                    log.error("failed to parse JWT token: {}", e.getMessage());
                }
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
