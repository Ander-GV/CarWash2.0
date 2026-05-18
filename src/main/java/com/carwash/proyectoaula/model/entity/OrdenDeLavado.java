package com.carwash.proyectoaula.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "OrdenDeLavado")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeLavado {

    @Id
    private String id;

    LocalDateTime fechaInicio;

    LocalDateTime fechaFin;

    double total;

    private String vehiculoId;

    private String clienteId;

    private String personalId;

    private String encargadoId;

    private String estado;

    private List<ServicioEmbebido> servicio;

    private String ordenCode;

    private String descripcionProblema;


}
