package com.carwash.proyectoaula.controller;

import com.carwash.proyectoaula.model.entity.Estado;
import com.carwash.proyectoaula.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    @Autowired
    private EstadoRepository estadoRepository;

    @GetMapping
    public ResponseEntity<List<Estado>> listarTodos() {
        return ResponseEntity.ok(estadoRepository.findAllByOrderByOrdenAsc());
    }

    @PostMapping
    public ResponseEntity<Estado> crearEstado(@RequestBody Estado nuevoEstado) {
        // Guardar siempre el nombre en mayúsculas para mantener consistencia
        if (nuevoEstado.getNombre() != null) {
            nuevoEstado.setNombre(nuevoEstado.getNombre().toUpperCase());
        }
        
        // Evitar duplicados
        if (estadoRepository.findByNombreIgnoreCase(nuevoEstado.getNombre()).isPresent()) {
            return ResponseEntity.badRequest().build(); // Ya existe
        }
        
        return ResponseEntity.ok(estadoRepository.save(nuevoEstado));
    }
}
