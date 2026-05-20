package com.carwash.proyectoaula.dto.personal;



import com.carwash.proyectoaula.dto.PersonaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalCreateDTO extends PersonaDTO {

    @NotNull(message = "El rol es obligatorio")
    private String rol;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;


    @NotNull(message = "La disponibilidad es obligatoria")
    private boolean disponibleHoy;
    
}
