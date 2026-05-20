package com.carwash.proyectoaula.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carwash.proyectoaula.dto.personal.PersonalCreateDTO;
import com.carwash.proyectoaula.dto.personal.PersonalResponseDTO;
import com.carwash.proyectoaula.dto.personal.PersonalUpdateDTO;
import com.carwash.proyectoaula.mapper.PersonalMapper;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Rol;
import com.carwash.proyectoaula.repository.PersonaRepository;
import com.carwash.proyectoaula.repository.RolRepository;
import com.carwash.proyectoaula.service.PersonalService;

// Implementación de la lógica para gestionar empleados, encargados y administradores
@Service
public class PersonalServiceImpl implements PersonalService {
    
    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PersonalMapper personalMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Genera automáticamente un código único según el rol (ej. EMP-0001)
    private String generarUserCode(String rol){
        String prefijo = switch(rol.toUpperCase()){
            case "ADMIN" -> "ADM";
            case "ENCARGADO" -> "ENC";
            case "EMPLEADO" -> "EMP";
            default -> "EMP";
        };
        
        Optional<Persona> ultimoDisponible = personaRepository.findTopByRolesNombreOrderByUserCodeDesc(rol.toUpperCase());
        if (ultimoDisponible.isEmpty()) return prefijo + "-0001";
        
        String ultimoUserCode = ultimoDisponible.get().getUserCode();
        if (!ultimoUserCode.contains("-")) {
            return prefijo + "-0001";
        }
        try {
            int numero = Integer.parseInt(ultimoUserCode.split("-")[1]);
            return prefijo + "-" + String.format("%04d", numero + 1);
        } catch (Exception e) {
            return prefijo + "-0001";
        }
    }

    
    // Devuelve la lista completa de todos los administradores
    @Override
    public List<PersonalResponseDTO> listarAdmin(){
        return personaRepository.findByRolesNombre("ADMIN")
        .stream()
        .map(personalMapper::toPersonalResponseDTO)
        .collect(Collectors.toList());
    }

    // Devuelve la lista completa de encargados
    @Override
    public List<PersonalResponseDTO> listarEncargado(){
        return personaRepository.findByRolesNombre("ENCARGADO")
        .stream()
        .map(personalMapper::toPersonalResponseDTO)
        .collect(Collectors.toList());
    }

    // Devuelve la lista completa de empleados
    @Override
    public List<PersonalResponseDTO> listarEmpleados(){
        return personaRepository.findByRolesNombre("EMPLEADO")
        .stream()
        .map(personalMapper::toPersonalResponseDTO)
        .collect(Collectors.toList());
    }


    // Busca a una persona específica de la plantilla por su código
    @Override
    public PersonalResponseDTO buscarPersonal(String userCode) {
        return personaRepository.findByUserCode(userCode)
        .map(personalMapper::toPersonalResponseDTO)
        .orElse(null);
    }

    // Contrata o registra un nuevo miembro del personal, validando que sus documentos no estén duplicados
    @Override
    public PersonalResponseDTO crearPersonal(PersonalCreateDTO dto){
        if (personaRepository.existsByDocumento(dto.getDocumento())) {
            throw new IllegalArgumentException("El documento ya existe");
        }
        if (personaRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya existe");
        }
        if (personaRepository.existsByTelefono(dto.getTelefono())){
            throw new IllegalArgumentException("El telefono ya existe");
        }

        Persona persona = personalMapper.toEntity(dto);
        persona.setUserCode(generarUserCode(dto.getRol()));
        persona.setPassword(passwordEncoder.encode(dto.getPassword()));
        return personalMapper.toPersonalResponseDTO(personaRepository.save(persona));
    }


    // Edita la información de un empleado, incluyendo si se le cambió de rol o contraseña
    @Override
    public PersonalResponseDTO actualizarPersonal(String userCode, PersonalUpdateDTO dto){

        Persona persona = personaRepository.findByUserCode(userCode)
        .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));

        if(dto.getRol() != null && !persona.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(dto.getRol()))){
            persona.getRoles().clear();
            Rol dbRol = rolRepository.findByNombreIgnoreCase(dto.getRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRol()));
            persona.getRoles().add(dbRol);
            persona.setUserCode(generarUserCode(dto.getRol()));
        }

        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setTelefono(dto.getTelefono());
        persona.setCorreo(dto.getCorreo());
        persona.setDireccion(dto.getDireccion());
        persona.setActivo(dto.isActivo());
        persona.setDisponibleHoy(dto.isDisponibleHoy());

        if(dto.getPassword() != null && !dto.getPassword().isBlank()){
            persona.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return personalMapper.toPersonalResponseDTO(personaRepository.save(persona));

    } 

    // Despide o elimina el registro de un miembro del personal permanentemente
    @Override
    public void eliminarPersonal(String userCode){
        Persona persona = personaRepository.findByUserCode(userCode)
        .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));
        personaRepository.delete(persona);
    }

    // Marca de forma rápida si un empleado asistió hoy a trabajar o está disponible
    @Override
    public void cambiarDisponibilidad(String userCode, boolean disponible){
        Persona persona = personaRepository.findByUserCode(userCode)
        .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));
        persona.setDisponibleHoy(disponible);
        personaRepository.save(persona);
    }

}

