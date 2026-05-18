package com.carwash.proyectoaula.model.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "servicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String serviceCode;

    private String nombre;

    private String descripcion;

    private List<PrecioTipo> precios;

    private boolean activo;
}
