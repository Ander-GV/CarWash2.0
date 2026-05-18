package com.carwash.proyectoaula.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.servicio.PrecioTipoDTO;
import com.carwash.proyectoaula.dto.servicio.ServicioCreateDTO;
import com.carwash.proyectoaula.dto.servicio.ServicioResponseDTO;
import com.carwash.proyectoaula.model.entity.PrecioTipo;
import com.carwash.proyectoaula.model.entity.Servicio;

@Component
public class ServicioMapper {
    
    public Servicio toServicio(ServicioCreateDTO dto){
        if(dto == null){
            return null;
        }

        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setActivo(true);
        servicio.setPrecios(toprecio(dto.getPrecios()));
        return servicio;
    }

    public ServicioResponseDTO toServicioResponseDTO(Servicio servicio){
        
        if(servicio == null){
            return null;
        }

        ServicioResponseDTO dto = new ServicioResponseDTO();
        dto.setServiceCode(servicio.getServiceCode());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setPrecios(toDto(servicio.getPrecios()));
        dto.setActivo(servicio.isActivo());
        return dto;

    }

    private List<PrecioTipo> toprecio(List<PrecioTipoDTO> dtos){
        if(dtos == null){
            return null;
        }
        return dtos.stream()
        .map(dto ->{
            PrecioTipo precio = new PrecioTipo();
            precio.setTipoDeVehiculoId(dto.getTipoVehiculoId());
            precio.setNombre(dto.getNombre());
            precio.setPrecio(dto.getPrecio());
            return precio;
        })
        .collect(Collectors.toList());
    }

    private List<PrecioTipoDTO> toDto(List<PrecioTipo> precios){
        if(precios == null){
            return null;
        }
        return precios.stream()
        .map(precio ->{
            PrecioTipoDTO dto = new PrecioTipoDTO();
            dto.setTipoVehiculoId(precio.getTipoDeVehiculoId());
            dto.setNombre(precio.getNombre());
            dto.setPrecio(precio.getPrecio());
            return dto;
        })
        .collect(Collectors.toList());
    }
}
