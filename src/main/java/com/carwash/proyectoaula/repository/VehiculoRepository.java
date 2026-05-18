package com.carwash.proyectoaula.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carwash.proyectoaula.model.entity.Vehiculo;


@Repository
public interface VehiculoRepository extends MongoRepository<Vehiculo, String>{
    
    Optional<Vehiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    List<Vehiculo> findByClienteId(String clienteId);

}
