package com.carwash.proyectoaula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carwash.proyectoaula.dto.ordenDeLavado.ordenUpdateEstadoDTO;

import com.carwash.proyectoaula.service.OrdenService;

@RestController
@RequestMapping("/api/test-patch")
public class TestController {

    @Autowired
    private OrdenService ordenService;

    @PostMapping("/{ordenCode}")
    public ResponseEntity<?> testPatch(@PathVariable String ordenCode) {
        try {
            ordenUpdateEstadoDTO dto = new ordenUpdateEstadoDTO();
            dto.setEstado("EN_PROCESO");
            return ResponseEntity.ok(ordenService.actualizarEstado(ordenCode, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }
}
