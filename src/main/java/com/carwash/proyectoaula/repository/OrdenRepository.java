package com.carwash.proyectoaula.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carwash.proyectoaula.model.entity.OrdenDeLavado;


@Repository
public interface  OrdenRepository extends MongoRepository<OrdenDeLavado, String>{
    
    Optional<OrdenDeLavado> findByOrdenCode(String ordenCode);
    Page<OrdenDeLavado> findByVehiculoId(String vehiculoId, Pageable pageable);
    Page<OrdenDeLavado> findByPersonalId(String personalId, Pageable pageable);
    Page<OrdenDeLavado> findByClienteId(String clienteId, Pageable pageable);
    Page<OrdenDeLavado> findByEstado(String estado, Pageable pageable);
    Page<OrdenDeLavado> findByPersonalIdAndFechaInicioBetween(String personalId, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    boolean existsByOrdenCode(String ordenCode);

}
