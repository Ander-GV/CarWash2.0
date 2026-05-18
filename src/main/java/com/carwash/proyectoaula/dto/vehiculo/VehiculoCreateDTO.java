package com.carwash.proyectoaula.dto.vehiculo;

import com.carwash.proyectoaula.model.entity.TipoVehiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoCreateDTO {
    
    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "El año es obligatorio")
    private String year;

    @NotNull(message = "El tipo de vehiculo es obligatorio")
    private TipoVehiculo tipoVehiculo;

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotBlank(message = "El propietario es obligatorio")
    private String documentoCliente;
}


