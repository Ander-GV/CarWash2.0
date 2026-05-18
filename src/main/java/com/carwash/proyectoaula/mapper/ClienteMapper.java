package com.carwash.proyectoaula.mapper;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.cliente.ClienteCreateDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteResponseDTO;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.enums.Rol;

import java.util.HashSet;
import java.util.Set;

@Component
public class ClienteMapper {
    
    public Persona toCliente(ClienteCreateDTO dto){
        if(dto == null){
            return null;
        }

        Persona persona = new Persona();
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setCorreo(dto.getCorreo());
        persona.setTelefono(dto.getTelefono());
        persona.setDireccion(dto.getDireccion());
        persona.setDocumento(dto.getDocumento());
        persona.setActivo(dto.isActivo());
        
        Set<Rol> roles = new HashSet<>();
        roles.add(Rol.CLIENTE);
        persona.setRoles(roles);
        
        return persona;
    }

    public ClienteResponseDTO toClienteResponseDTO(Persona persona){
        if(persona == null){
            return null;
        }

        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setNombre(persona.getNombre());
        dto.setApellido(persona.getApellido());
        dto.setCorreo(persona.getCorreo());
        dto.setTelefono(persona.getTelefono());
        dto.setDireccion(persona.getDireccion());
        dto.setDocumento(persona.getDocumento());
        dto.setActivo(persona.isActivo());
        dto.setUserCode(persona.getUserCode());
        dto.setId(persona.getId());
        return dto;
    }
}
