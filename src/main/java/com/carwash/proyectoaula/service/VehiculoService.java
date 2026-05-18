package com.carwash.proyectoaula.service;

import java.util.List;

import com.carwash.proyectoaula.dto.vehiculo.VehiculoCreateDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoResponseDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoUpdateDTO;

/**
 * Interfaz de servicio para la gestión de la lógica de negocio de Vehículos.
 * Define los contratos para las operaciones CRUD y lógicas adicionales.
 */
public interface VehiculoService {

    // Obtener lista de vehículos de un cliente
    List<VehiculoResponseDTO> listarVehiculos(String userCode);

    // Buscar vehículo por placa
    VehiculoResponseDTO buscarVehiculo(String placa);

    // Guardar nuevo vehículo
    VehiculoResponseDTO crearVehiculo(VehiculoCreateDTO dto);

    // Actualizar datos del vehículo
    VehiculoResponseDTO actualizarVehiculo(String placa, VehiculoUpdateDTO dto);

    // Borrar/Ocultar vehículo
    void eliminarVehiculo(String placa);
}
