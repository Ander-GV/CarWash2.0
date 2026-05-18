package com.carwash.proyectoaula.service;

import java.util.List;

import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoCreateDTO;
import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoResponseDTO;

public interface  TipoVehiculoService {
    
    TipoDeVehiculoResponseDTO crear(TipoDeVehiculoCreateDTO dto);
    List<TipoDeVehiculoResponseDTO> listarActivos();
    List<TipoDeVehiculoResponseDTO> listarTodos();
    void desactivar(String id);
    void activar(String id);
    void eliminar(String id);
}
