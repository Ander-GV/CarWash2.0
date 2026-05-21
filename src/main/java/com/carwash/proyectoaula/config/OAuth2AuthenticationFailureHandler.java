package com.carwash.proyectoaula.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        String targetUrl = "/index.html?error=";
        
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            if ("email_not_registered".equals(oauth2Exception.getError().getErrorCode())) {
                targetUrl += "not_registered";
            } else if ("user_inactive".equals(oauth2Exception.getError().getErrorCode())) {
                targetUrl += "user_inactive";
            } else {
                targetUrl += "auth_failed";
            }
        } else {
            targetUrl += "auth_failed";
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
