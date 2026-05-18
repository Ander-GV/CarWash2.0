package com.carwash.proyectoaula.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Servicio encargado de enviar correos electrónicos a través de N8N
@Service
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    // URL del webhook configurado en N8N
    @org.springframework.beans.factory.annotation.Value("${n8n.webhook.email.url:http://localhost:5678/webhook/enviar-correo-cliente}")
    private String N8N_WEBHOOK_URL;

    // Envía un enlace de actualización de datos por correo al cliente
    public void enviarTokenActualizacion(String destino, String token) {
        String urlActualizacion = "http://localhost:8080/actualizar-datos.html?token=" + token;
        
        System.out.println("=================================================");
        System.out.println("🚀 ENVIANDO WEBHOOK A N8N PARA CORREO A: " + destino);
        System.out.println("LINK PARA ACTUALIZAR DATOS: " + urlActualizacion);
        System.out.println("=================================================");

        try {
            // Construir el payload JSON con los datos del correo
            Map<String, String> payload = new HashMap<>();
            payload.put("email", destino);
            payload.put("urlActualizacion", urlActualizacion);
            payload.put("asunto", "CarWash - Actualiza tus datos");

            // Enviar petición POST a n8n
            restTemplate.postForEntity(N8N_WEBHOOK_URL, payload, String.class);
            System.out.println("Webhook enviado a n8n exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al comunicar con n8n. Asegúrate de que n8n esté corriendo y el webhook esté activo.");
        }
    }
}
