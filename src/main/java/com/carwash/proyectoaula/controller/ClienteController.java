package com.carwash.proyectoaula.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.proyectoaula.dto.cliente.ClienteCreateDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteResponseDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteUpdateDTO;
import com.carwash.proyectoaula.service.ClienteService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;


    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<ClienteResponseDTO> buscarCliente(@PathVariable String userCode) {
        return ResponseEntity.ok(clienteService.buscarCliente(userCode));
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteResponseDTO> buscarDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(clienteService.buscarDocumento(documento));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(@Valid @RequestBody ClienteCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(dto));
    }

    @PutMapping("/{userCode}")
    public ResponseEntity<?> actualizarCliente(@PathVariable String userCode, @Valid @RequestBody ClienteUpdateDTO dto, org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isOwner = userCode.equals(authentication.getName());
        boolean hasPrivileges = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_ENCARGADO"));

        if (!isOwner && !hasPrivileges) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(java.util.Map.of("error", "No tienes permisos para actualizar esta información."));
        }

        return ResponseEntity.ok(clienteService.actualizarCliente(userCode, dto));
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String userCode) {
        clienteService.eliminarCliente(userCode);
        return ResponseEntity.noContent().build();
    }


}