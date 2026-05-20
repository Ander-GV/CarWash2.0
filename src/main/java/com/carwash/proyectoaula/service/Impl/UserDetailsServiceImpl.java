package com.carwash.proyectoaula.service.Impl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.repository.PersonaRepository;

// Servicio encargado de decirle a Spring Security cómo buscar y cargar usuarios desde la base de datos
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private PersonaRepository personaR;

    // Método principal de Spring Security que se ejecuta al intentar iniciar sesión
    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        Persona persona = personaR.findByUserCode(userCode)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userCode));
        
        // Si es un cliente que inició por Google, su password será null y no puede entrar con contraseña normal
        if (persona.getPassword() == null) {
            throw new UsernameNotFoundException("El usuario no tiene contraseña registrada (ingreso por correo/OAuth)");
        }

        // Construimos el objeto interno de Spring Security con los roles del usuario (ej. ROLE_ADMIN)
        return new User(
            persona.getUserCode(),
            persona.getPassword(),
            persona.isActivo(),
            true,
            true,
            true,
            persona.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList())
        );
    }
}