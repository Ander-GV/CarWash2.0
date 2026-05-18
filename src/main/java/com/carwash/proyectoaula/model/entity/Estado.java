package com.carwash.proyectoaula.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "estados")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estado {

    @Id
    private String id;

    private String nombre;

    private String descripcion;
    
    private String color;
    
    private String textColor;
    
    private Integer orden;
    
    private Boolean esIncidencia;
    
    private Boolean esFinal;
}
