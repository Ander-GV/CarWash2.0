package com.carwash.proyectoaula.mapper;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.cliente.ClienteCreateDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteResponseDTO;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Rol;
import com.carwash.proyectoaula.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Component
public class ClienteMapper {

    @Autowired
    private RolRepository rolRepository;
    
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
        Rol clienteRol = rolRepository.findByNombreIgnoreCase("CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado en base de datos"));
        roles.add(clienteRol);
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
