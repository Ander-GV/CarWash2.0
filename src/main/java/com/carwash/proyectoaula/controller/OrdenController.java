package com.carwash.proyectoaula.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenCreateDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenResponseDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.ordenUpdateEstadoDTO;
import com.carwash.proyectoaula.service.OrdenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<Page<OrdenResponseDTO>> listarTodas(
            @PageableDefault(sort = "fechaInicio", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ordenService.listarTodas(pageable));
    }

    @GetMapping("/{ordenCode}")
    public ResponseEntity<OrdenResponseDTO> obtenerPorCodigo(@PathVariable String ordenCode) {
        return ResponseEntity.ok(ordenService.obtenerPorCodigo(ordenCode));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<OrdenResponseDTO>> listarPorCliente(
            @PathVariable String clienteId, 
            @PageableDefault(sort = "fechaInicio", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ordenService.listarPorClientes(clienteId, pageable));
    }

    @GetMapping("/personal/{personalId}")
    public ResponseEntity<Page<OrdenResponseDTO>> listarPorPersonal(
            @PathVariable String personalId, 
            @PageableDefault(sort = "fechaInicio", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ordenService.listarPorPersonal(personalId, pageable));
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<Page<OrdenResponseDTO>> listarPorVehiculo(
            @PathVariable String vehiculoId, 
            @PageableDefault(sort = "fechaInicio", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ordenService.listarPorVehiculo(vehiculoId, pageable));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<OrdenResponseDTO>> listarPorEstado(
            @PathVariable String estado, 
            @PageableDefault(sort = "fechaInicio", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ordenService.listarPorEstado(estado, pageable));
    }

    @PatchMapping("/{ordenCode}/estado")
    public ResponseEntity<OrdenResponseDTO> actualizarEstado(
            @PathVariable String ordenCode,
            @Valid @RequestBody ordenUpdateEstadoDTO dto) {
        return ResponseEntity.ok(ordenService.actualizarEstado(ordenCode, dto));
    }
}