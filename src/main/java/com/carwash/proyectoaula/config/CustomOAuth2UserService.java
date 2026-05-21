package com.carwash.proyectoaula.config;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.carwash.proyectoaula.model.entity.ClienteDetalles;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Rol;
import com.carwash.proyectoaula.repository.PersonaRepository;
import com.carwash.proyectoaula.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerId = oAuth2User.getAttribute("sub"); // ID único de Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        // Buscar SIEMPRE por correo, para asegurar que si cambiaron su correo, 
        // solo puedan entrar con la cuenta de Google que tenga ese nuevo correo.
        Optional<Persona> personaOpt = personaRepository.findByCorreoIgnoreCase(email);

        Persona persona;
        if (personaOpt.isPresent()) {
            persona = personaOpt.get();
            boolean changed = false;
            
            // Asegurar rol CLIENTE
            boolean tieneClienteRol = persona.getRoles().stream()
                .anyMatch(r -> r.getNombre().equalsIgnoreCase("CLIENTE"));
            if (!tieneClienteRol) {
                Rol clienteRol = rolRepository.findByNombreIgnoreCase("CLIENTE")
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado en base de datos"));
                persona.getRoles().add(clienteRol);
                changed = true;
            }

            // Actualizar información del proveedor si falta o si el cliente cambió de correo y ahora usa otro ID de Google
            if (persona.getClienteDetalles() == null) {
                persona.setClienteDetalles(new ClienteDetalles(provider, providerId));
                changed = true;
            } else if (!providerId.equals(persona.getClienteDetalles().getProviderId())) {
                persona.getClienteDetalles().setAuthProvider(provider);
                persona.getClienteDetalles().setProviderId(providerId);
                changed = true;
            }

            if (!persona.isClienteActivo()) {
                throw new OAuth2AuthenticationException("user_inactive");
            }

            if (changed) {
                personaRepository.save(persona);
            }
        } else {
            // El cliente no existe en la base de datos, denegar acceso. (Requerimiento de negocio)
            throw new OAuth2AuthenticationException("email_not_registered");
        }

        Collection<? extends GrantedAuthority> authorities = persona.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList());

        // Retornar un DefaultOAuth2User con los roles reales
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email" // la clave del atributo de nombre
        );
    }
}
