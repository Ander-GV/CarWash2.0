package com.carwash.proyectoaula.dto.tipoVehiculo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TipoDeVehiculoResponseDTO {
    
    private String id;
    private String nombre;
    private boolean activo;

}
