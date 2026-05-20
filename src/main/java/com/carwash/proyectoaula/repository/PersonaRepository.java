package com.carwash.proyectoaula.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Rol;

@Repository
public interface PersonaRepository extends MongoRepository<Persona, String> {

    Optional<Persona> findByCorreoIgnoreCase(String correo);
    Optional<Persona> findByCorreo(String correo);

    Optional<Persona> findByUserCode(String userCode);

    boolean existsByCorreo(String correo);

    boolean existsByDocumento(String documento);
    
    Optional<Persona> findByDocumento(String documento);

    Optional<Persona> findByClienteDetalles_ProviderId(String providerId);

    boolean existsByTelefono(String telefono);

    List<Persona> findByRolesContaining(Rol rol);

    Optional<Persona> findTopByRolesContainingOrderByUserCodeDesc(Rol rol);

    @org.springframework.data.mongodb.repository.Query("{ 'roles.nombre': ?0 }")
    List<Persona> findByRolesNombre(String nombre);

    @org.springframework.data.mongodb.repository.Query("{ 'roles.nombre': ?0 }")
    Optional<Persona> findTopByRolesNombreOrderByUserCodeDesc(String nombre);

    Optional<Persona> findTopByUserCodeStartingWithOrderByUserCodeDesc(String prefix);
}
