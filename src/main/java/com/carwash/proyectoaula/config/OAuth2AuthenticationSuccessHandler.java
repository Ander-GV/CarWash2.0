package com.carwash.proyectoaula.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.repository.PersonaRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final PersonaRepository personaRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");


        Optional<Persona> personaOpt = personaRepository.findByCorreoIgnoreCase(email);
        
        if (personaOpt.isPresent()) {
            Persona persona = personaOpt.get();
            

            String token = jwtUtil.generarToken(persona.getUserCode(), "CLIENTE");

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setAttribute("SameSite", "Strict");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            String targetUrl = "/cliente/index.html";
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {

            String targetUrl = "/index.html?error=oauth2_failure";
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
