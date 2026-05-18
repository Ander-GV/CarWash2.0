package com.carwash.proyectoaula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

// Servicio encargado de gestionar los tokens invalidados en Redis
@Service
public class TokenBlacklistService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Agrega un token a la lista negra al cerrar sesión
    public void blacklistToken(String token) {
        // Guarda el token en la lista negra por 10 horas (mismo tiempo de expiración)
        redisTemplate.opsForValue().set("blacklist:" + token, "true", Duration.ofHours(10));
    }

    // Verifica si el token existe en la lista negra de Redis
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}
