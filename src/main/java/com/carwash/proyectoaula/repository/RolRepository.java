package com.carwash.proyectoaula.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carwash.proyectoaula.model.entity.Rol;

@Repository
public interface RolRepository extends MongoRepository<Rol, String> {
    Optional<Rol> findByNombreIgnoreCase(String nombre);
    Optional<Rol> findByNombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
