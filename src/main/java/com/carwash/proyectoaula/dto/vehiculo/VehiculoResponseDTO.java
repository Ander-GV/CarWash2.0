package com.carwash.proyectoaula.dto.vehiculo;

import com.carwash.proyectoaula.model.entity.TipoVehiculo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoResponseDTO {
    
    private String id;
    private String marca;
    private String color;
    private String modelo;
    private String year;
    private TipoVehiculo tipoVehiculo;
    private String placa;
    private String clienteId;
    private boolean activo;
}
