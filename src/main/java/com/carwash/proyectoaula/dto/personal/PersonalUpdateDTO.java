package com.carwash.proyectoaula.dto.personal;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalUpdateDTO {

    

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotNull(message = "El rol es obligatorio")
    private String rol;

    @NotNull(message = "El estado es obligatorio")
    private boolean activo;

    private boolean disponibleHoy;

    private String password;

}
