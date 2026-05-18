package com.carwash.proyectoaula.service;

import java.util.List;

import com.carwash.proyectoaula.dto.servicio.ServicioCreateDTO;
import com.carwash.proyectoaula.dto.servicio.ServicioResponseDTO;

public interface ServicioService {
    
    ServicioResponseDTO crearServicio(ServicioCreateDTO dto);
    ServicioResponseDTO buscarPorCodigo(String serviceCode);
    List<ServicioResponseDTO> listarActivos();
    List<ServicioResponseDTO> listarTodos();
    ServicioResponseDTO actualizar(String serviceCode, ServicioCreateDTO dto);
    void desactivar(String serviceCode);
    void activar(String serviceCode);
    void eliminar(String serviceCode);
}
