package com.carwash.proyectoaula.mapper;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoCreateDTO;
import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoResponseDTO;
import com.carwash.proyectoaula.model.entity.TipoVehiculo;

@Component
public class TipoDeVehiculoMapper {
    
    public TipoVehiculo toTipo(TipoDeVehiculoCreateDTO dto){
        if(dto == null){
            return null;
        }
        TipoVehiculo tipoVehiculo = new TipoVehiculo();
        tipoVehiculo.setNombre(dto.getNombre());
        tipoVehiculo.setActivo(true);
        return tipoVehiculo;
    }


    public TipoDeVehiculoResponseDTO toTipoResponse(TipoVehiculo tipoVehiculo){
        if(tipoVehiculo == null){
            return null;
        }
        TipoDeVehiculoResponseDTO dto = new TipoDeVehiculoResponseDTO();
        dto.setId(tipoVehiculo.getId());
        dto.setNombre(tipoVehiculo.getNombre());
        dto.setActivo(tipoVehiculo.getActivo());
        return dto;
    }
}