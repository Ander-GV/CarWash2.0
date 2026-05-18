package com.carwash.proyectoaula.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank(message = "El userCode es obligatorio")
    private String userCode;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}

