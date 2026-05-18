package com.carwash.proyectoaula.dto.tipoVehiculo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TipoDeVehiculoCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
}
