package com.carwash.proyectoaula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carwash.proyectoaula.model.entity.TipoVehiculo;

@Repository 
public interface  TipoVehiculoRepository extends MongoRepository<TipoVehiculo, String> {
    
    Optional<TipoVehiculo> findByNombre(String nombre);
    List<TipoVehiculo> findAllByActivoTrue();
    boolean existsByNombre(String nombre);

}
