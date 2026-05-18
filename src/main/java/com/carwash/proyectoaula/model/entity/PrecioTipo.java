package com.carwash.proyectoaula.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrecioTipo {
    
    private String tipoDeVehiculoId;
    private Double precio;
    private String nombre;

}
