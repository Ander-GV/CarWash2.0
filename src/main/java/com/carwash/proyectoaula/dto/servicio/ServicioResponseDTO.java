package com.carwash.proyectoaula.dto.servicio;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ServicioResponseDTO {
    
    private String serviceCode;
    private String nombre;
    private String descripcion;
    private List<PrecioTipoDTO> precios;
    private boolean activo;

}
