package com.carwash.proyectoaula.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoCreateDTO;
import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoResponseDTO;
import com.carwash.proyectoaula.service.TipoVehiculoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/tipovehiculo")
public class TipoVehiculoController {
    
    @Autowired
    private TipoVehiculoService tipoVehiculoService;

    @PostMapping
    public ResponseEntity<TipoDeVehiculoResponseDTO> crear(@RequestBody TipoDeVehiculoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoVehiculoService.crear(dto));
    }
    
    @GetMapping("/todos")
    public ResponseEntity<List<TipoDeVehiculoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(tipoVehiculoService.listarTodos());
    }

    @GetMapping
    public ResponseEntity<List<TipoDeVehiculoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(tipoVehiculoService.listarActivos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        tipoVehiculoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable String id) {
        tipoVehiculoService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/eliminar")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        tipoVehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
