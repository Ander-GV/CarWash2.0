package com.carwash.proyectoaula.dto.cliente;

import com.carwash.proyectoaula.dto.PersonaDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO extends PersonaDTO {
    private String id;
    private String userCode;
}
