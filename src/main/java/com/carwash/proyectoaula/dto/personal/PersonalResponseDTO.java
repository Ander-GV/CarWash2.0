package com.carwash.proyectoaula.dto.personal;

import com.carwash.proyectoaula.dto.PersonaDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalResponseDTO extends PersonaDTO {
    private String id;
    private String rol;
    private String userCode;
    private boolean disponibleHoy;
    
}
