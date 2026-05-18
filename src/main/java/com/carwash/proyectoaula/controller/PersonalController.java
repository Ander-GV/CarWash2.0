package com.carwash.proyectoaula.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.proyectoaula.dto.personal.PersonalCreateDTO;
import com.carwash.proyectoaula.dto.personal.PersonalResponseDTO;
import com.carwash.proyectoaula.dto.personal.PersonalUpdateDTO;
import com.carwash.proyectoaula.service.PersonalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {
    
    @Autowired
    private PersonalService personalService;

    @GetMapping("/admin")
    public ResponseEntity<List<PersonalResponseDTO>> listarAdmin() {
        return ResponseEntity.ok(personalService.listarAdmin());
    }

    @GetMapping("/encargado")
    public ResponseEntity<List<PersonalResponseDTO>> listarEncargado() {
        return ResponseEntity.ok(personalService.listarEncargado());   
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<PersonalResponseDTO>> listarEmpleados() {
        return ResponseEntity.ok(personalService.listarEmpleados());
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<PersonalResponseDTO> buscarPersonal(@PathVariable String userCode) {
        return ResponseEntity.ok(personalService.buscarPersonal(userCode));
    }

    @PostMapping
    public ResponseEntity<PersonalResponseDTO> crearPersonal(@Valid @RequestBody PersonalCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalService.crearPersonal(dto));
    }

    @PutMapping("/{userCode}")
    public ResponseEntity<PersonalResponseDTO> actualizarPersonal(@PathVariable String userCode, @Valid @RequestBody PersonalUpdateDTO dto) {
        return ResponseEntity.ok(personalService.actualizarPersonal(userCode, dto));
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<Void> eliminarPersonal(@PathVariable String userCode) {
        personalService.eliminarPersonal(userCode);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{userCode}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(@PathVariable String userCode, @RequestParam boolean disponible) {
        personalService.cambiarDisponibilidad(userCode, disponible);
        return ResponseEntity.ok().build();
    }

}