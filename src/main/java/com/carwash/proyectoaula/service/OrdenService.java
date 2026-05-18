package com.carwash.proyectoaula.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenCreateDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenResponseDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.ordenUpdateEstadoDTO;

public interface OrdenService {
    
    // Crear una nueva orden de lavado
    OrdenResponseDTO crear(OrdenCreateDTO dto);

    // Actualizar el estado de una orden (ej. de PENDIENTE a FINALIZADO)
    OrdenResponseDTO actualizarEstado(String ordenCode,ordenUpdateEstadoDTO dto);

    // Buscar una orden específica por su código
    OrdenResponseDTO obtenerPorCodigo(String ordenCode);

    // Ver el historial de órdenes de un cliente
    Page<OrdenResponseDTO> listarPorClientes(String clienteId, Pageable pageable);

    // Ver las órdenes asignadas a un empleado/encargado
    Page<OrdenResponseDTO> listarPorPersonal(String personalId, Pageable pageable);

    // Ver el historial de órdenes de una placa de vehículo
    Page<OrdenResponseDTO> listarPorVehiculo(String vehiculoId, Pageable pageable);

    // Filtrar todas las órdenes según su estado actual
    Page<OrdenResponseDTO> listarPorEstado(String estado, Pageable pageable);

    // Traer todas las órdenes (paginadas)
    Page<OrdenResponseDTO> listarTodas(Pageable pageable);
}

