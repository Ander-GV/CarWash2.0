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

import com.carwash.proyectoaula.dto.vehiculo.VehiculoCreateDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoResponseDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoUpdateDTO;
import com.carwash.proyectoaula.service.VehiculoService;




@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VehiculoResponseDTO>> listarVehiculos(@PathVariable String clienteId) {
        return ResponseEntity.ok(vehiculoService.listarVehiculos(clienteId));
    }

    @GetMapping("/{placa}")
    public ResponseEntity<VehiculoResponseDTO> obtenerVehiculo(@PathVariable String placa) {
        return ResponseEntity.ok(vehiculoService.buscarVehiculo(placa));
    }
    
    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> CrearVehiculo(@RequestBody VehiculoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crearVehiculo(dto));
    }

    @PutMapping("/{placa}")
    public ResponseEntity<VehiculoResponseDTO> actualizarVehiculo(@PathVariable String placa, @RequestBody VehiculoUpdateDTO dto) {
        return ResponseEntity.ok(vehiculoService.actualizarVehiculo(placa, dto));
    }
    
    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable String placa) {
        vehiculoService.eliminarVehiculo(placa);
        return ResponseEntity.noContent().build();
    }


}
