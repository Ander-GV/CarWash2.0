package com.carwash.proyectoaula.dto.personal;

import com.carwash.proyectoaula.dto.PersonaDTO;
import com.carwash.proyectoaula.model.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalResponseDTO extends PersonaDTO {
    private String id;
    private Rol rol;
    private String userCode;
    private boolean disponibleHoy;
    
}
