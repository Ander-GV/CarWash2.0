package com.carwash.proyectoaula.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long EXPIRACION = 10 * 60 * 60 * 1000; // 10 horas en milisegundos

    // Secret inyectado desde application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generar un token JWT estándar para inicio de sesión
    public String generarToken(String userCode, String rol) {
        return Jwts.builder()
                .subject(userCode)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRACION))
                .signWith(key)
                .compact();
    }

    // Generar un token con 24 horas de validez para actualizar datos
    public String generarTokenActualizacion(String userCode) {
        return Jwts.builder()
                .subject(userCode)
                .claim("purpose", "update_data")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))) // 24 horas
                .signWith(key)
                .compact();
    }

    // Generar un token con 24 horas de validez para actualizar datos
    public String generarTokenActualizacion(String userCode) {
        return Jwts.builder()
                .subject(userCode)
                .claim("purpose", "update_data")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))) // 24 horas
                .signWith(key)
                .compact();
    }

    // Obtener el código de usuario a partir del token
    public String extraerUserCode(String token) {
        return getClaims(token).getSubject();
    }

    // Obtener el rol (ej. ADMIN, EMPLEADO) a partir del token
    public String extraerRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    // Ahora distingue expirado vs. inválido
    public boolean validarToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e; // deja que el filtro lo maneje con 401 "token expirado"
        } catch (JwtException | IllegalArgumentException e) {
            return false; // firma inválida, token malformado, etc.
        }
    }

    // Útil para refresh token flows
    public boolean estaExpirado(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)   // usa el key ya inicializado
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}