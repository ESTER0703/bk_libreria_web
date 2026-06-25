package com.biblioteca.api.security;

import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String header = request.getHeader("Authorization");
        String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;

        boolean publicReadPath = (path.equals("/api/books") || path.startsWith("/api/books/")) && "GET".equalsIgnoreCase(method);
        boolean publicAuthPath = path.equals("/api/auth/login") || path.equals("/api/auth/register");
        boolean protectedPath = path.startsWith("/api/users") || path.startsWith("/api/loans") || path.startsWith("/api/reports") || path.startsWith("/api/reservations");

        if (token != null && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            Optional<User> u = userRepository.findById(userId);
            u.ifPresent(user -> request.setAttribute("authUser", user));
        } else if (!publicReadPath && !publicAuthPath && protectedPath) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Unauthorized\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
