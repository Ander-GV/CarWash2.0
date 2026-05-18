package com.carwash.proyectoaula.dto.vehiculo;

import com.carwash.proyectoaula.model.entity.TipoVehiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class VehiculoUpdateDTO {

        @NotBlank(message = "El color es obligatorio")
        private String color;

        @NotBlank(message = "El modelo es obligatorio")
        private String modelo;
        
        @NotBlank(message = "El año es obligatorio")
        private String year;
        
        @NotBlank(message = "La marca es obligatoria")
        private String marca;

        @NotNull(message = "El tipo de vehiculo es obligatorio")
        private TipoVehiculo tipoVehiculo; 

        @NotNull(message = "El estado del vehiculo es obligatorio")
        private Boolean activo;
}