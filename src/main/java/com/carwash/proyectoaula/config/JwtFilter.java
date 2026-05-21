package com.carwash.proyectoaula.config;


import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Filtro que intercepta CADA petición HTTP para verificar si el usuario tiene un token válido
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final com.carwash.proyectoaula.service.TokenBlacklistService tokenBlacklistService;

    public JwtFilter(JwtUtil jwtUtil, com.carwash.proyectoaula.service.TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = null;

        // Primero busca el token en las cookies del navegador
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && !token.isEmpty()) {

            // Si el token está en la lista negra (cerró sesión), se rechaza de inmediato
            if (tokenBlacklistService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token invalidado (Logout)\"}");
                return;
            }

            try {
                // Verifica que el token sea auténtico matemáticamente
                if (jwtUtil.validarToken(token)) {
                    String userCode = jwtUtil.extraerUserCode(token);
                    String rol = jwtUtil.extraerRol(token);

                    // Crea la sesión interna temporal en Spring Security con el rol correspondiente
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            userCode,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token inválido\"}");
                    return;
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expirado\"}");
                return;
            }
        }

        // Si todo está bien (o si es una ruta pública sin token), permite que la petición continúe
        chain.doFilter(request, response);
    }
}