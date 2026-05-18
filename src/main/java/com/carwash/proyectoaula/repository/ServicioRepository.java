package com.carwash.proyectoaula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carwash.proyectoaula.model.entity.Servicio;

@Repository
public interface ServicioRepository extends MongoRepository<Servicio, String> {
    
    Optional<Servicio> findByServiceCode(String serviceCode);
    List<Servicio> findAllByActivoTrue();
    boolean existsByServiceCode(String serviceCode);
    int countByServiceCodeStartingWith(String prefix);


}
