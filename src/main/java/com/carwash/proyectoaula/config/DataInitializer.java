package com.carwash.proyectoaula.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Rol;
import com.carwash.proyectoaula.repository.PersonaRepository;
import com.carwash.proyectoaula.repository.RolRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(PersonaRepository personaRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder, org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
        return args -> {
            String[] rolesDefinidos = {"ADMIN", "ENCARGADO", "EMPLEADO", "CLIENTE"};
            for (String rName : rolesDefinidos) {
                if (!rolRepository.existsByNombreIgnoreCase(rName)) {
                    Rol newRol = new Rol();
                    newRol.setNombre(rName);
                    rolRepository.save(newRol);
                }
            }

            try {
                org.bson.Document query = new org.bson.Document();
                java.util.List<org.bson.Document> rawPersonas = mongoTemplate.getCollection("personas").find(query).into(new java.util.ArrayList<>());
                for (org.bson.Document doc : rawPersonas) {
                    Object rolesObj = doc.get("roles");
                    if (rolesObj instanceof java.util.List) {
                        java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
                        boolean needsMigration = false;
                        java.util.List<org.bson.Document> migratedRoles = new java.util.ArrayList<>();
                        
                        for (Object r : rolesList) {
                            if (r instanceof String) {
                                String rName = (String) r;
                                needsMigration = true;
                                Rol dbRol = rolRepository.findByNombreIgnoreCase(rName)
                                    .orElseGet(() -> {
                                        Rol newRol = new Rol();
                                        newRol.setNombre(rName.toUpperCase());
                                        return rolRepository.save(newRol);
                                    });
                                
                                Object idVal;
                                if (dbRol.getId() != null && org.bson.types.ObjectId.isValid(dbRol.getId())) {
                                    idVal = new org.bson.types.ObjectId(dbRol.getId());
                                } else {
                                    idVal = dbRol.getId();
                                }
                                
                                org.bson.Document dbRolDoc = new org.bson.Document();
                                dbRolDoc.put("_id", idVal);
                                dbRolDoc.put("nombre", dbRol.getNombre());
                                migratedRoles.add(dbRolDoc);
                            } else if (r instanceof org.bson.Document) {
                                migratedRoles.add((org.bson.Document) r);
                            }
                        }
                        
                        if (needsMigration) {
                            doc.put("roles", migratedRoles);
                            mongoTemplate.getCollection("personas").replaceOne(new org.bson.Document("_id", doc.get("_id")), doc);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR DataInitializer al migrar roles: " + e.getMessage());
            }

            java.util.List<Persona> todas = personaRepository.findAll();
            java.util.Map<String, java.util.List<Persona>> agrupadas = todas.stream()
                .filter(p -> p.getUserCode() != null)
                .collect(java.util.stream.Collectors.groupingBy(Persona::getUserCode));
            
            for (java.util.Map.Entry<String, java.util.List<Persona>> entry : agrupadas.entrySet()) {
                java.util.List<Persona> duplicados = entry.getValue();
                if (duplicados.size() > 1) {
                    for (int i = 1; i < duplicados.size(); i++) {
                        personaRepository.delete(duplicados.get(i));
                    }
                }
            }

            todas = personaRepository.findAll();
            java.util.Map<String, java.util.List<Persona>> agrupadasPorCorreo = todas.stream()
                .filter(p -> p.getCorreo() != null && !p.getCorreo().trim().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(p -> p.getCorreo().toLowerCase().trim()));
            
            for (java.util.Map.Entry<String, java.util.List<Persona>> entry : agrupadasPorCorreo.entrySet()) {
                java.util.List<Persona> duplicados = entry.getValue();
                if (duplicados.size() > 1) {
                    for (int i = 1; i < duplicados.size(); i++) {
                        personaRepository.delete(duplicados.get(i));
                    }
                }
            }

            // Siempre garantizar que exista el usuario admin de prueba con contraseña correcta
            String encodedPass = passwordEncoder.encode("admin123");

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
            Rol adminRol = rolRepository.findByNombreIgnoreCase("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado en base de datos"));
            roles.add(adminRol);
            adminTest.setRoles(roles);
            adminTest.setUserCode("admin");
            adminTest.setPassword(encodedPass);
            adminTest.setDisponibleHoy(true);
            personaRepository.save(adminTest);

            // Asegurar de emergencia que cualquier administrador real (como ADM-0001) esté activo siempre
            java.util.List<Persona> todosLosAdmins = personaRepository.findByRolesNombre("ADMIN");
            for (Persona p : todosLosAdmins) {
                if (!p.isActivo()) {
                    p.setActivo(true);
                    personaRepository.save(p);
                }
            }
        };
    }
}
