package com.carwash.proyectoaula.dto.ordenDeLavado;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdenCreateDTO {
    
    @NotBlank(message = "El vehiculo es obligatorio")
    private String vehiculoId;

    @NotBlank(message = "El personal es obligatorio")
    private String personalId;

    @NotBlank(message = "El cliente es obligatorio")
    private String clienteId;

    @NotEmpty(message = "Debes seleccionar al menos un servicio")
    private List<String>  serviceCode;



}
