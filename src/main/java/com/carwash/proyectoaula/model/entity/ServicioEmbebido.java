package com.carwash.proyectoaula.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicioEmbebido {
    
    private String serviceCode;

    private String nombre;

    private String tipoDeVehiculo;

    private double precioFinal;
}
