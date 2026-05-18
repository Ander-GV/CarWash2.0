package com.carwash.proyectoaula.dto.servicio;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ServicioCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "Los precios son obligatorios")
    private List<PrecioTipoDTO> precios;
    

}
