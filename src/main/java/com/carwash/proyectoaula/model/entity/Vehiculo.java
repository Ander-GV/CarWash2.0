package com.carwash.proyectoaula.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document (collection = "vehiculos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehiculo {
    
    @Id
    private String id;

    private TipoVehiculo tipoVehiculo;

    @Indexed(unique = true)
    private String placa;

    private String color;

    private String modelo;

    private String year;

    private String marca;

    private String clienteId;

    private Boolean activo;

}
