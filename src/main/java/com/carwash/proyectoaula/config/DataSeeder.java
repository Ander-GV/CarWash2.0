package com.carwash.proyectoaula.config;

import com.carwash.proyectoaula.model.entity.Estado;
import com.carwash.proyectoaula.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private EstadoRepository estadoRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> estadosPorDefecto = Arrays.asList(
            "PENDIENTE",
            "EN_PROCESO",
            "ESPERANDO_CONFIRMACION",
            "FINALIZADO",
            "CANCELADO",
            "CON_PROBLEMAS"
        );

        for (String nombreEstado : estadosPorDefecto) {
            if (estadoRepository.findByNombreIgnoreCase(nombreEstado).isEmpty()) {
                Estado nuevoEstado = new Estado();
                nuevoEstado.setNombre(nombreEstado);
                nuevoEstado.setDescripcion("Estado: " + nombreEstado);
                estadoRepository.save(nuevoEstado);
                System.out.println("Seeded estado: " + nombreEstado);
            }
        }
    }
}
