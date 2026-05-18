package com.carwash.proyectoaula.mapper;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.vehiculo.VehiculoCreateDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoResponseDTO;
import com.carwash.proyectoaula.model.entity.Vehiculo;

@Component
public class VehiculoMapper {

    public Vehiculo toVehiculo(VehiculoCreateDTO dto){
        if(dto == null){
            return null;
        }
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setColor(dto.getColor());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setYear(dto.getYear());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setTipoVehiculo(dto.getTipoVehiculo());
        return vehiculo;
    }

    public VehiculoResponseDTO toVehiculoResponseDTO(Vehiculo vehiculo){
        if(vehiculo == null){
            return null;
        }
        VehiculoResponseDTO dto = new VehiculoResponseDTO();
        dto.setPlaca(vehiculo.getPlaca());
        dto.setColor(vehiculo.getColor());
        dto.setModelo(vehiculo.getModelo());
        dto.setYear(vehiculo.getYear());
        dto.setMarca(vehiculo.getMarca());
        dto.setTipoVehiculo(vehiculo.getTipoVehiculo());
        dto.setId(vehiculo.getId());
        dto.setClienteId(vehiculo.getClienteId());
        dto.setActivo(vehiculo.getActivo());
        return dto;
    }
    
}
