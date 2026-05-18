package com.carwash.proyectoaula.mapper;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.personal.PersonalCreateDTO;
import com.carwash.proyectoaula.dto.personal.PersonalResponseDTO;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.enums.Rol;

import java.util.HashSet;
import java.util.Set;

@Component
public class PersonalMapper {

    public Persona toEntity(PersonalCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        Persona persona = new Persona();
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setDocumento(dto.getDocumento());
        persona.setTelefono(dto.getTelefono());
        persona.setCorreo(dto.getCorreo());
        persona.setDireccion(dto.getDireccion());
        persona.setActivo(dto.isActivo());
        
        Set<Rol> roles = new HashSet<>();
        if (dto.getRol() != null) {
            roles.add(dto.getRol());
        }
        persona.setRoles(roles);
        
        persona.setPassword(dto.getPassword());
        persona.setDisponibleHoy(dto.isDisponibleHoy());
        return persona;
    }

    public PersonalResponseDTO toPersonalResponseDTO(Persona persona) {
        if (persona == null) {
            return null;
        }
        PersonalResponseDTO dto = new PersonalResponseDTO();
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setDocumento(persona.getDocumento());
        dto.setTelefono(persona.getTelefono());
        dto.setCorreo(persona.getCorreo());
        dto.setDireccion(persona.getDireccion());
        dto.setActivo(persona.isActivo());
        
        // Asignar rol principal para el DTO (priorizando roles administrativos)
        if (persona.getRoles() != null && !persona.getRoles().isEmpty()) {
            Rol principal = persona.getRoles().stream()
                .filter(r -> r != Rol.CLIENTE)
                .findFirst()
                .orElse(Rol.CLIENTE);
            dto.setRol(principal);
        }
        
        dto.setUserCode(persona.getUserCode());
        dto.setId(persona.getId());
        dto.setDisponibleHoy(persona.isDisponibleHoy());
        return dto;
    }
}
