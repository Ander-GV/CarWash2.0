package com.carwash.proyectoaula.config;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String validationMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validación fallida",
                "message", validationMessage,
                "path", request.getRequestURI()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleConflict(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Conflicto de datos",
                "message", ex.getMessage() == null ? "Conflicto de validación" : ex.getMessage(),
                "path", request.getRequestURI()
        ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleBusinessRuntime(
            RuntimeException ex,
            HttpServletRequest request) {
        String message = ex.getMessage() == null ? "Error de negocio" : ex.getMessage();
        String lowerMessage = message.toLowerCase();

        if (lowerMessage.contains("no encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", HttpStatus.NOT_FOUND.value(),
                    "error", "Recurso no encontrado",
                    "message", message,
                    "path", request.getRequestURI()
            ));
        }

        if (lowerMessage.contains("ya existe") || lowerMessage.contains("ya esta registrado")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", HttpStatus.CONFLICT.value(),
                    "error", "Conflicto de datos",
                    "message", message,
                    "path", request.getRequestURI()
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Solicitud inválida",
                "message", message,
                "path", request.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericError(
            Exception ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Error interno del servidor",
                "message", ex.getMessage() == null ? "Error inesperado" : ex.getMessage(),
                "path", request.getRequestURI()
        ));
    }
}

