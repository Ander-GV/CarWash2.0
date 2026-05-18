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

import com.carwash.proyectoaula.dto.servicio.ServicioCreateDTO;
import com.carwash.proyectoaula.dto.servicio.ServicioResponseDTO;
import com.carwash.proyectoaula.service.ServicioService;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;
    
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(@RequestBody ServicioCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crearServicio(dto));
    }

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listar(){
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ServicioResponseDTO>> listarTodos(){
        return ResponseEntity.ok(servicioService.listarTodos());
    }

    @GetMapping("/{serviceCode}")
    public ResponseEntity<ServicioResponseDTO> buscarPorCodigo(@PathVariable String serviceCode){
        return ResponseEntity.ok(servicioService.buscarPorCodigo(serviceCode));
    }

    @PutMapping("/{serviceCode}")
    public ResponseEntity<ServicioResponseDTO> actualizar(@PathVariable String serviceCode, @RequestBody ServicioCreateDTO dto){
        return ResponseEntity.ok(servicioService.actualizar(serviceCode, dto));
    }

    @DeleteMapping("/{serviceCode}")
    public ResponseEntity<Void> desactivar(@PathVariable String serviceCode){
        servicioService.desactivar(serviceCode);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{serviceCode}/activar")
    public ResponseEntity<Void> activar(@PathVariable String serviceCode){
        servicioService.activar(serviceCode);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{serviceCode}/eliminar")
    public ResponseEntity<Void> eliminar(@PathVariable String serviceCode){
        servicioService.eliminar(serviceCode);
        return ResponseEntity.noContent().build();
    }
}
