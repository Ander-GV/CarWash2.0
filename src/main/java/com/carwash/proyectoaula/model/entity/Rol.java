package com.carwash.proyectoaula.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "roles")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rol {

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombre; // "ADMIN", "ENCARGADO", "EMPLEADO", "CLIENTE"
}
