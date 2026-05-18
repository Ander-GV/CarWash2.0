package com.carwash.proyectoaula.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tiposVehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoVehiculo {

    @Id
    private String id;

    private String nombre;

    private Boolean activo;
}