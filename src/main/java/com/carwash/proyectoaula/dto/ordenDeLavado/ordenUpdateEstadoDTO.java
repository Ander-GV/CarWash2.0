package com.carwash.proyectoaula.dto.ordenDeLavado;

import java.time.LocalDateTime;



import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ordenUpdateEstadoDTO {
    
    // Aceptamos el String si lo mandan
    private String estado;

    // Aceptamos el ID viejo para que el frontend no se rompa
    private Integer estadoId;

    private String fechaFin;

    private String descripcionProblema;
    
    public LocalDateTime getFechaFinAsLocalDateTime() {
        if (this.fechaFin == null || this.fechaFin.isEmpty()) {
            return null;
        }
        try {
            // Eliminar la 'Z' y milisegundos si vienen de JavaScript toISOString()
            String parseable = this.fechaFin;
            if (parseable.endsWith("Z")) {
                parseable = parseable.substring(0, parseable.length() - 1);
            }
            if (parseable.contains(".")) {
                parseable = parseable.substring(0, parseable.indexOf("."));
            }
            return LocalDateTime.parse(parseable);
        } catch (Exception e) {
            return null;
        }
    }

    // Si el frontend manda un ID, lo convertimos automáticamente a String
    public String getEstado() {
        if (this.estado != null && !this.estado.isEmpty()) {
            return this.estado;
        }
        if (this.estadoId != null) {
            switch(this.estadoId) {
                case 1: return "PENDIENTE";
                case 2: return "EN_PROCESO";
                case 3: return "ESPERANDO_CONFIRMACION";
                case 4: return "FINALIZADO";
                case 5: return "CANCELADO";
                case 6: return "CON_PROBLEMAS";
                default: return "PENDIENTE";
            }
        }
        return null;
    }
}
