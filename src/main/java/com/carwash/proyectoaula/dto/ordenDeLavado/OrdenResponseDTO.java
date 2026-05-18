package com.carwash.proyectoaula.dto.ordenDeLavado;

import java.time.LocalDateTime;
import java.util.List;

import com.carwash.proyectoaula.model.entity.ServicioEmbebido;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenResponseDTO {
    
    private String ordenCode;

    private String vehiculoId;

    private String personalId;

    private String encargadoId;

    private String clienteId;

    private List<ServicioEmbebido> servicio;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private double total;

    private String estado;

    private Integer estadoId;

    private String descripcionProblema;
}
