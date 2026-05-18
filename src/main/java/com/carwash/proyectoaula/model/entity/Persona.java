package com.carwash.proyectoaula.model.entity;

import java.util.Set;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.carwash.proyectoaula.model.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "personas")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Persona {

    @Id
    private String id;

    private String nombre;
    private String apellido;   
    private String documento;
    private String telefono;
    
    @Indexed(unique = true)
    private String correo;   
    private String direccion;
    private boolean activo;

    @Indexed(unique = true)
    private String userCode;

    // Campos de personal
    private Set<Rol> roles = new HashSet<>();
    private String password; // Nulo para clientes OAuth
    private boolean disponibleHoy;

    // Campos de cliente embebidos
    private ClienteDetalles clienteDetalles;
}
