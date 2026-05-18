package com.carwash.proyectoaula.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteUpdateDTO {
    
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;


    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(min = 10, max = 10, message = "El telefono debe tener entre 10 caracteres")
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser valido")
    private String correo;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;
}
