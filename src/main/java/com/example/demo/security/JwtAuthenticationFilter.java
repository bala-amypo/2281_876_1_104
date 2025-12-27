package com.example.demo.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        
        if (request.getServletPath().startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if (jwtTokenProvider.validateToken(token)) {

                String email = jwtTokenProvider.getUsername(token);
                String role = jwtTokenProvider.getRole(token); 

                if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    //  add ROLE_ prefix
                    SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + role);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                        );

                    authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
