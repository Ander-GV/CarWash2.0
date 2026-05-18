package com.carwash.proyectoaula.mapper;

import org.springframework.stereotype.Component;

import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenCreateDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenResponseDTO;
import com.carwash.proyectoaula.model.entity.OrdenDeLavado;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OrdenMapper {


    public OrdenDeLavado toOrden(OrdenCreateDTO dto){
        if(dto == null){
            return null;
        }

        OrdenDeLavado orden = new OrdenDeLavado();
        orden.setClienteId(dto.getClienteId());
        orden.setVehiculoId(dto.getVehiculoId());
        orden.setPersonalId(dto.getPersonalId());
        orden.setFechaInicio(java.time.LocalDateTime.now());
        orden.setEstado("PENDIENTE");
        return orden;
    }

    public OrdenResponseDTO toResponse(OrdenDeLavado orden){
        if(orden == null){
            return null;
        }

        OrdenResponseDTO dto = new OrdenResponseDTO();
        if (orden.getOrdenCode() != null && !orden.getOrdenCode().isEmpty()) {
            dto.setOrdenCode(orden.getOrdenCode());
        } else {
            dto.setOrdenCode(orden.getId());
        }
        dto.setVehiculoId(orden.getVehiculoId());
        dto.setPersonalId(orden.getPersonalId());
        dto.setEncargadoId(orden.getEncargadoId());
        dto.setClienteId(orden.getClienteId());
        dto.setServicio(orden.getServicio());
        dto.setFechaInicio(orden.getFechaInicio());
        dto.setFechaFin(orden.getFechaFin());
        dto.setTotal(orden.getTotal());
        dto.setEstado(orden.getEstado());
        
        // Mapeo inverso para que el frontend (que esperaba estadoId) siga funcionando sin cambios
        if (orden.getEstado() != null) {
            switch (orden.getEstado().toUpperCase()) {
                case "PENDIENTE": dto.setEstadoId(1); break;
                case "EN_PROCESO": dto.setEstadoId(2); break;
                case "ESPERANDO_CONFIRMACION": dto.setEstadoId(3); break;
                case "FINALIZADO": dto.setEstadoId(4); break;
                case "CANCELADO": dto.setEstadoId(5); break;
                case "CON_PROBLEMAS": dto.setEstadoId(6); break;
                default: dto.setEstadoId(1);
            }
        } else {
            dto.setEstadoId(1);
        }

        dto.setDescripcionProblema(orden.getDescripcionProblema());
        return dto;
    }
}
