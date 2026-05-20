package com.carwash.proyectoaula.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.proyectoaula.config.JwtUtil;
import com.carwash.proyectoaula.dto.AuthRequestDTO;
import com.carwash.proyectoaula.service.TokenBlacklistService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO dto, jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUserCode(), dto.getPassword())
            );


            String authority = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .filter(a -> !a.equals("ROLE_CLIENTE"))
                    .findFirst()
                    .orElseGet(() -> authentication.getAuthorities().stream()
                            .findFirst()
                            .map(a -> a.getAuthority())
                            .orElse("ROLE_USER"));

            String rol = authority.startsWith("ROLE_") ? authority.substring("ROLE_".length()) : authority;
            String token = jwtUtil.generarToken(dto.getUserCode(), rol);

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setAttribute("SameSite", "Lax");
            // Set Secure flag if request is HTTPS or proxied as HTTPS
            String xForwardedProto = request.getHeader("X-Forwarded-Proto");
            boolean isSecure = request.isSecure() || "https".equalsIgnoreCase(xForwardedProto);
            cookie.setSecure(isSecure);
            cookie.setMaxAge(3600);

            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("rol", rol));
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "El usuario se encuentra inactivo"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        String authority = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> !a.equals("ROLE_CLIENTE"))
                .findFirst()
                .orElseGet(() -> authentication.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority())
                        .orElse("ROLE_USER"));
        String rol = authority.startsWith("ROLE_") ? authority.substring("ROLE_".length()) : authority;

        return ResponseEntity.ok(Map.of(
                "userCode", authentication.getName(),
                "rol", rol,
                "authenticated", true
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) {

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName()) && cookie.getValue() != null) {
                    tokenBlacklistService.blacklistToken(cookie.getValue());
                    break;
                }
            }
        }

        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        boolean isSecure = request.isSecure() || "https".equalsIgnoreCase(xForwardedProto);
        cookie.setSecure(isSecure);
        cookie.setMaxAge(0); 
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
    }
}

