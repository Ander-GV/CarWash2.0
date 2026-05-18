package com.carwash.proyectoaula.service;

import java.util.List;

import com.carwash.proyectoaula.dto.personal.PersonalCreateDTO;
import com.carwash.proyectoaula.dto.personal.PersonalResponseDTO;
import com.carwash.proyectoaula.dto.personal.PersonalUpdateDTO;


public interface PersonalService {
    
    List<PersonalResponseDTO> listarAdmin();
    List<PersonalResponseDTO> listarEncargado();
    List<PersonalResponseDTO> listarEmpleados();
    PersonalResponseDTO buscarPersonal(String userCode);
    PersonalResponseDTO crearPersonal(PersonalCreateDTO dto);
    PersonalResponseDTO actualizarPersonal(String userCode, PersonalUpdateDTO dto);
    void cambiarDisponibilidad(String userCode, boolean disponible);
    void eliminarPersonal(String userCode);
}
