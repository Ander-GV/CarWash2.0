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

        String requestURI = request.getRequestURI();
        // Evitamos loguear peticiones a recursos estáticos para no saturar los logs
        if (!requestURI.contains("/css/") && !requestURI.contains("/js/") && !requestURI.contains("/favicon.ico")) {
            System.out.println("DEBUG JWT_FILTER: Interceptando " + request.getMethod() + " " + requestURI);
            if (token == null) {
                System.out.println("DEBUG JWT_FILTER: Cookie 'token' NO encontrada.");
            } else {
                System.out.println("DEBUG JWT_FILTER: Cookie 'token' encontrada. Longitud: " + token.length());
            }
        }

        if (token != null && !token.isEmpty()) {

            // Si el token está en la lista negra (cerró sesión), se rechaza de inmediato
            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("DEBUG JWT_FILTER: El token está en la LISTA NEGRA de Redis.");
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
                    if (!requestURI.contains("/css/") && !requestURI.contains("/js/")) {
                        System.out.println("DEBUG JWT_FILTER: Token válido. Usuario: " + userCode + ", Rol: " + rol);
                    }

                    // Crea la sesión interna temporal en Spring Security con el rol correspondiente
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            userCode,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    System.out.println("DEBUG JWT_FILTER: Token inválido (firma o estructura incorrecta).");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token inválido\"}");
                    return;
                }
            } catch (ExpiredJwtException e) {
                System.out.println("DEBUG JWT_FILTER: Token expirado.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expirado\"}");
                return;
            } catch (Exception e) {
                System.out.println("DEBUG JWT_FILTER: Error inesperado al procesar token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Error de autenticación\"}");
                return;
            }
        }

        // Si todo está bien (o si es una ruta pública sin token), permite que la petición continúe
        chain.doFilter(request, response);
    }
}