package com.carwash.proyectoaula.service.Impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenCreateDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.OrdenResponseDTO;
import com.carwash.proyectoaula.dto.ordenDeLavado.ordenUpdateEstadoDTO;
import com.carwash.proyectoaula.mapper.OrdenMapper;
import com.carwash.proyectoaula.model.entity.OrdenDeLavado;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.PrecioTipo;
import com.carwash.proyectoaula.model.entity.Servicio;
import com.carwash.proyectoaula.model.entity.ServicioEmbebido;
import com.carwash.proyectoaula.model.entity.Vehiculo;

import com.carwash.proyectoaula.repository.OrdenRepository;
import com.carwash.proyectoaula.repository.PersonaRepository;
import com.carwash.proyectoaula.repository.ServicioRepository;
import com.carwash.proyectoaula.repository.VehiculoRepository;
import com.carwash.proyectoaula.repository.EstadoRepository;
import com.carwash.proyectoaula.service.OrdenService;

@Service
public class OrdenServiceImpl implements OrdenService{
    
    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private EstadoRepository estadoRepository;



    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private OrdenMapper ordenMapper;


    private String generarCodigo(){
        String codigo;
        do { 
            codigo = "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (ordenRepository.existsByOrdenCode(codigo));
        return codigo;
    }

    // Crear una nueva orden de lavado
    @Override
    public OrdenResponseDTO crear(OrdenCreateDTO dto){

        Vehiculo vehiculo = vehiculoRepository.findByPlaca(dto.getVehiculoId())
        .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con placa: " + dto.getVehiculoId()));

        Persona cliente = personaRepository.findByUserCode(dto.getClienteId())
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar que el vehículo pertenezca al cliente especificado
        if (!vehiculo.getClienteId().equals(cliente.getId())) {
            throw new RuntimeException("El vehículo seleccionado no está registrado a nombre del cliente indicado. Verifique la placa.");
        }

        if(vehiculo.getTipoVehiculo() == null){
            throw new RuntimeException("El tipo de vehículo no se encuentra registrado en el sistema.");
        }

        List<ServicioEmbebido> serviciosEmbebidos = new ArrayList<>();
        double total = 0.0;

        // Recorrer los servicios solicitados y calcular el total
        for(String serviceCode : dto.getServiceCode()){
            Servicio servicio = servicioRepository.findByServiceCode(serviceCode)
            .orElseThrow(() -> new RuntimeException("El servicio seleccionado no está disponible (" + serviceCode + ")."));

            if (servicio.getPrecios() == null) {
                throw new RuntimeException("El servicio '" + servicio.getNombre() + "' no está configurado para el tipo de vehículo: " + vehiculo.getTipoVehiculo().getNombre() + ". Por favor, pídale al Administrador que agregue un precio para este servicio.");
            }

            PrecioTipo precioTipo = servicio.getPrecios().stream()
            .filter(precio -> precio.getTipoDeVehiculoId().equals(vehiculo.getTipoVehiculo().getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("El servicio '" + servicio.getNombre() + "' no está configurado para el tipo de vehículo: " + vehiculo.getTipoVehiculo().getNombre() + ". Por favor, pídale al Administrador que agregue un precio para este servicio."));
            
            ServicioEmbebido embebido = new ServicioEmbebido();
            
            embebido.setServiceCode(serviceCode);
            embebido.setNombre(servicio.getNombre());
            embebido.setTipoDeVehiculo(vehiculo.getTipoVehiculo().getNombre());
            embebido.setPrecioFinal(precioTipo.getPrecio());

            // Agregar al listado final y sumar al precio total
            serviciosEmbebidos.add(embebido);
            total += precioTipo.getPrecio();
        }

        OrdenDeLavado orden = ordenMapper.toOrden(dto);
        
        // Obtener al empleado/encargado actual en sesión
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            orden.setEncargadoId(auth.getName());
        }

        // Validar que el estado inicial (PENDIENTE) exista en la tabla maestra
        estadoRepository.findByNombreIgnoreCase("PENDIENTE")
            .orElseThrow(() -> new RuntimeException("El estado 'PENDIENTE' no existe en la base de datos maestra de estados"));

        orden.setServicio(serviciosEmbebidos);
        orden.setTotal(total);
        orden.setOrdenCode(generarCodigo());

        return ordenMapper.toResponse(ordenRepository.save(orden));

    }

    @Override
    public OrdenResponseDTO obtenerPorCodigo(String ordenCode){
        OrdenDeLavado orden = ordenRepository.findByOrdenCode(ordenCode).orElse(null);
        if (orden == null) {
            try {
                orden = ordenRepository.findById(ordenCode).orElse(null);
            } catch (IllegalArgumentException e) {
                // Not a valid MongoDB ObjectId hex string
            }
        }
        if (orden == null) {
            throw new RuntimeException("La orden de lavado solicitada no existe o ha sido eliminada.");
        }
        return ordenMapper.toResponse(orden);
    }

    @Override
    public Page<OrdenResponseDTO> listarPorEstado(String estado, Pageable pageable){
        return ordenRepository.findByEstado(estado, pageable)
        .map(ordenMapper::toResponse);
    }

    @Override
    public Page<OrdenResponseDTO> listarPorClientes(String clienteId, Pageable pageable){
        return ordenRepository.findByClienteId(clienteId, pageable)
        .map(ordenMapper::toResponse);
    }

    @Override
    public Page<OrdenResponseDTO> listarPorPersonal(String personalId, Pageable pageable){
        LocalDateTime fechaInicio = LocalDate.now().atStartOfDay();
        LocalDateTime fechaFin = LocalDate.now().atTime(23, 59, 59);
        
        return ordenRepository.findByPersonalIdAndFechaInicioBetween(personalId, fechaInicio, fechaFin, pageable)
        .map(ordenMapper::toResponse);
    }

    @Override
    public Page<OrdenResponseDTO> listarPorVehiculo(String vehiculoId, Pageable pageable){
        return ordenRepository.findByVehiculoId(vehiculoId, pageable)
        .map(ordenMapper::toResponse);
    }

    @Override
    public OrdenResponseDTO actualizarEstado(String ordenCode,ordenUpdateEstadoDTO dto){
        
        OrdenDeLavado orden = ordenRepository.findByOrdenCode(ordenCode).orElse(null);
        if (orden == null) {
            try {
                orden = ordenRepository.findById(ordenCode).orElse(null);
            } catch (IllegalArgumentException e) {
                // Not a valid MongoDB ObjectId hex string
            }
        }
        if (orden == null) {
            throw new RuntimeException("La orden de lavado solicitada no existe o ha sido eliminada.");
        }

        // Validar contra la tabla maestra de estados
        estadoRepository.findByNombreIgnoreCase(dto.getEstado())
            .orElseThrow(() -> new RuntimeException("El estado '" + dto.getEstado() + "' no existe en la base de datos maestra de estados"));

        orden.setEstado(dto.getEstado());

        if(dto.getFechaFin() != null){
            orden.setFechaFin(dto.getFechaFin());
        } else if ("FINALIZADO".equalsIgnoreCase(dto.getEstado()) || "CANCELADO".equalsIgnoreCase(dto.getEstado())) {
            orden.setFechaFin(LocalDateTime.now());
        }

        if ("CON_PROBLEMAS".equalsIgnoreCase(dto.getEstado()) || "CANCELADO".equalsIgnoreCase(dto.getEstado())) {
            if ("CON_PROBLEMAS".equalsIgnoreCase(dto.getEstado()) && (dto.getDescripcionProblema() == null || dto.getDescripcionProblema().trim().isEmpty())) {
                throw new RuntimeException("Por favor, detalle el problema o la incidencia ocurrida antes de guardar la orden en este estado.");
            }
            orden.setDescripcionProblema(dto.getDescripcionProblema());
            orden.setTotal(0.0); // Eliminar cargos y comisiones por la incidencia o cancelación
        } else if (dto.getDescripcionProblema() != null && !dto.getDescripcionProblema().trim().isEmpty()) {
            // Permitir guardar la descripción si se envía por algún otro motivo
            orden.setDescripcionProblema(dto.getDescripcionProblema());
        }
        OrdenDeLavado ordenGuardada = ordenRepository.save(orden);

        // Notificación automática a n8n cuando se finaliza o hay problemas
        if (("FINALIZADO".equalsIgnoreCase(dto.getEstado()) || "CON_PROBLEMAS".equalsIgnoreCase(dto.getEstado())) && ordenGuardada.getFechaFin() != null) {
            String encargadoCode = "";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                encargadoCode = auth.getName();
            }
            final String finalEncargadoCode = encargadoCode;
            
            CompletableFuture.runAsync(() -> {
                try {
                    Persona cliente = personaRepository.findByUserCode(ordenGuardada.getClienteId()).orElse(null);
                    Persona empleado = personaRepository.findByUserCode(ordenGuardada.getPersonalId()).orElse(null);
                    Persona encargado = personaRepository.findByUserCode(finalEncargadoCode).orElse(null);
                    
                    // Dentro de Docker, el backend se comunica con n8n usando su nombre de servicio 'n8n' en lugar de 'localhost'
                    String url = "http://n8n:5678/webhook/d39d8412-2d0d-41ed-b051-9b988c4cedd9";
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("ordenCode", ordenGuardada.getOrdenCode());
                    payload.put("vehiculoPlaca", ordenGuardada.getVehiculoId());
                    payload.put("estado", dto.getEstado());
                    payload.put("nombreCliente", cliente != null ? cliente.getNombre() + " " + cliente.getApellido() : "Cliente");
                    payload.put("correoCliente", cliente != null ? cliente.getCorreo() : "Sin correo");
                    payload.put("nombreEmpleado", empleado != null ? empleado.getNombre() + " " + empleado.getApellido() : "Empleado de turno");
                    payload.put("nombreEncargado", encargado != null ? encargado.getNombre() + " " + encargado.getApellido() : "Encargado de turno");
                    payload.put("descripcionProblema", ordenGuardada.getDescripcionProblema() != null ? ordenGuardada.getDescripcionProblema() : "");
                    payload.put("total", String.valueOf(ordenGuardada.getTotal()));
                    
                    StringBuilder serviciosStr = new StringBuilder();
                    if (ordenGuardada.getServicio() != null) {
                        for (com.carwash.proyectoaula.model.entity.ServicioEmbebido s : ordenGuardada.getServicio()) {
                            serviciosStr.append("- ").append(s.getNombre()).append(" ($").append(s.getPrecioFinal()).append(")\n");
                        }
                    }
                    payload.put("serviciosRealizados", serviciosStr.toString().trim());
                    
                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.postForObject(url, request, String.class);
                    System.out.println("Notificación n8n enviada exitosamente para la orden: " + ordenGuardada.getOrdenCode());
                } catch (Exception e) {
                    System.err.println("Error al enviar notificación a n8n: " + e.getMessage());
                }
            });
        }

        return ordenMapper.toResponse(ordenGuardada);
    }

    @Override
    public Page<OrdenResponseDTO> listarTodas(Pageable pageable){
        return ordenRepository.findAll(pageable)
        .map(ordenMapper::toResponse);
    }

}


