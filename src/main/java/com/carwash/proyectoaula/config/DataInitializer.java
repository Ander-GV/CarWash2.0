package com.carwash.proyectoaula.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.enums.Rol;
import com.carwash.proyectoaula.repository.PersonaRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(PersonaRepository personaRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Limpiar duplicados de 'userCode' en la base de datos para evitar fallos de Query no única
            java.util.List<Persona> todas = personaRepository.findAll();
            java.util.Map<String, java.util.List<Persona>> agrupadas = todas.stream()
                .filter(p -> p.getUserCode() != null)
                .collect(java.util.stream.Collectors.groupingBy(Persona::getUserCode));
            
            for (java.util.Map.Entry<String, java.util.List<Persona>> entry : agrupadas.entrySet()) {
                java.util.List<Persona> duplicados = entry.getValue();
                if (duplicados.size() > 1) {
                    System.out.println("DEBUG DataInitializer: Detectados " + duplicados.size() + " registros para el userCode '" + entry.getKey() + "'. Limpiando duplicados...");
                    // Conservamos el primero y borramos el resto
                    for (int i = 1; i < duplicados.size(); i++) {
                        personaRepository.delete(duplicados.get(i));
                    }
                }
            }

            // 2. Limpiar duplicados de 'correo' en la base de datos
            todas = personaRepository.findAll();
            java.util.Map<String, java.util.List<Persona>> agrupadasPorCorreo = todas.stream()
                .filter(p -> p.getCorreo() != null && !p.getCorreo().trim().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(p -> p.getCorreo().toLowerCase().trim()));
            
            for (java.util.Map.Entry<String, java.util.List<Persona>> entry : agrupadasPorCorreo.entrySet()) {
                java.util.List<Persona> duplicados = entry.getValue();
                if (duplicados.size() > 1) {
                    System.out.println("DEBUG DataInitializer: Detectados " + duplicados.size() + " registros para el correo '" + entry.getKey() + "'. Limpiando duplicados...");
                    // Conservamos el primero y borramos el resto
                    for (int i = 1; i < duplicados.size(); i++) {
                        personaRepository.delete(duplicados.get(i));
                    }
                }
            }

            // Siempre garantizar que exista el usuario admin de prueba con contraseña correcta
            String encodedPass = passwordEncoder.encode("admin123");
            System.out.println("DEBUG DataInitializer: Hash generado para admin123: " + encodedPass);

            // Crear o actualizar el usuario 'admin' de prueba
            Persona adminTest = personaRepository.findByUserCode("admin")
                .orElse(new Persona());
            adminTest.setNombre("Admin");
            adminTest.setApellido("Test");
            adminTest.setDocumento("9999999999");
            adminTest.setTelefono("3000000000");
            adminTest.setCorreo("admin@test.local");
            adminTest.setDireccion("Sistema CarWash");
            adminTest.setActivo(true);
            Set<Rol> roles = new HashSet<>();
            roles.add(Rol.ADMIN);
            adminTest.setRoles(roles);
            adminTest.setUserCode("admin");
            adminTest.setPassword(encodedPass);
            adminTest.setDisponibleHoy(true);
            personaRepository.save(adminTest);
            System.out.println("DEBUG DataInitializer: Usuario 'admin' creado/actualizado con contraseña 'admin123'");
        };
    }
}
