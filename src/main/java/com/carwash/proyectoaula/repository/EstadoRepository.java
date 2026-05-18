package com.carwash.proyectoaula.repository;

import com.carwash.proyectoaula.model.entity.Estado;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de los estados de las órdenes en la base de datos MongoDB.
 */
@Repository
public interface EstadoRepository extends MongoRepository<Estado, String> {
    
    // Busca un estado por nombre exacto sin importar mayúsculas
    Optional<Estado> findByNombreIgnoreCase(String nombre);
    
    // Trae todos los estados ordenados por su número de orden
    java.util.List<Estado> findAllByOrderByOrdenAsc();
}
