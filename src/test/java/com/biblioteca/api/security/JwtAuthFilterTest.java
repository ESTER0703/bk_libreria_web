package com.biblioteca.api.security;

import com.biblioteca.api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    @Test
    void allowsPublicBookListingWithoutAuthentication() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserRepository userRepository = mock(UserRepository.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userRepository);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/books");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void blocksReservationCreationWithoutAuthentication() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserRepository userRepository = mock(UserRepository.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userRepository);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/reservations");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
    }
}
