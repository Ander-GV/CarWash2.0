package com.carwash.proyectoaula.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDetalles {
    private String authProvider; 
    private String providerId;   
}
