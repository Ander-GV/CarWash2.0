package com.carwash.proyectoaula.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.carwash.proyectoaula.dto.cliente.ClienteCreateDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteResponseDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteUpdateDTO;
import com.carwash.proyectoaula.mapper.ClienteMapper;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Rol;
import com.carwash.proyectoaula.repository.PersonaRepository;
import com.carwash.proyectoaula.service.ClienteService;


// Implementación de la lógica de negocio para gestionar Clientes
@Service
public class ClienteServiceImpl implements ClienteService {
    
    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ClienteMapper clienteMapper;




    // Generar un código único secuencial para cada cliente (ej. CLI-0001)
    public String generadorUserCode(){
        long count = personaRepository.count(); // Aproximación, mejor buscar el max
        return "CLI-" + String.format("%04d", count + 1); // Simplificado para no fallar con formatos variados
    }

    // Listar a todos los usuarios que tienen el rol de CLIENTE
    @Override
    public List<ClienteResponseDTO> listarClientes(){
        return personaRepository.findByRolesNombre("CLIENTE")
        .stream()
        .map(clienteMapper::toClienteResponseDTO)
        .collect(Collectors.toList());
    }


    // Buscar la información de un cliente a través de su userCode
    @Override
    public ClienteResponseDTO buscarCliente(String userCode){
        return personaRepository.findByUserCode(userCode)
        .filter(p -> p.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase("CLIENTE")))
        .map(clienteMapper::toClienteResponseDTO)
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    } 

    // Registrar a un nuevo cliente validando que sus datos sean únicos
    @Override
    public ClienteResponseDTO crearCliente(ClienteCreateDTO dto){
    
        if(personaRepository.existsByTelefono(dto.getTelefono())){
            throw new RuntimeException("El telefono ya esta registrado");
        }
        if(personaRepository.existsByDocumento(dto.getDocumento())){
            throw new RuntimeException("El documento ya esta registrado");
        }
        if(personaRepository.existsByCorreo(dto.getCorreo())){
            throw new RuntimeException("El correo ya esta registrado");
        }

        Persona persona = clienteMapper.toCliente(dto);
        persona.setUserCode(generadorUserCode());
        Persona guardado = personaRepository.save(persona);



        return clienteMapper.toClienteResponseDTO(guardado);

    }


    // Actualizar datos de un cliente existente
    @Override
    @CacheEvict(value = "users", key = "#userCode")
    public ClienteResponseDTO actualizarCliente(String userCode, ClienteUpdateDTO dto){
        
        Persona persona = personaRepository.findByUserCode(userCode)
        .filter(p -> p.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase("CLIENTE")))
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setTelefono(dto.getTelefono());
        persona.setCorreo(dto.getCorreo());
        persona.setDireccion(dto.getDireccion());
        persona.setClienteActivo(dto.isActivo());

        return clienteMapper.toClienteResponseDTO(personaRepository.save(persona));
    }

    // Borrar cliente de forma definitiva
    @Override
    @CacheEvict(value = "users", key = "#userCode")
    public void eliminarCliente(String userCode){
        Persona persona = personaRepository.findByUserCode(userCode)
        .filter(p -> p.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase("CLIENTE")))
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        personaRepository.delete(persona);
    }

    // Utilidad extra para buscar clientes por su documento de identidad (CC, etc.)
    @Override
    public ClienteResponseDTO buscarDocumento(String documento){
        return personaRepository.findByDocumento(documento)
        .map(clienteMapper::toClienteResponseDTO)
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

}