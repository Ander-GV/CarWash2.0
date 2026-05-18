package com.carwash.proyectoaula.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.carwash.proyectoaula.mapper.ServicioMapper;
import com.carwash.proyectoaula.dto.servicio.ServicioCreateDTO;
import com.carwash.proyectoaula.dto.servicio.ServicioResponseDTO;
import com.carwash.proyectoaula.repository.ServicioRepository;
import com.carwash.proyectoaula.service.ServicioService;
import com.carwash.proyectoaula.model.entity.Servicio;

@Service
public class ServicioServiceImpl implements ServicioService{
    
    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ServicioMapper servicioMapper;

    private String generarCodigo(){
        int count = servicioRepository.countByServiceCodeStartingWith("SRV-");
        return "SRV-" + String.format("%04d", count + 1);
    }

    @Override
    public ServicioResponseDTO crearServicio(ServicioCreateDTO dto){
        Servicio servicio = servicioMapper.toServicio(dto);
        servicio.setServiceCode(generarCodigo());
        return servicioMapper.toServicioResponseDTO(servicioRepository.save(servicio));
    }

    @Override
    public ServicioResponseDTO buscarPorCodigo(String serviceCode){
        Servicio servicio = servicioRepository.findByServiceCode(serviceCode)
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado" +  serviceCode));
        return servicioMapper.toServicioResponseDTO(servicio);
    }

    @Override
    public List<ServicioResponseDTO> listarActivos(){
        List<Servicio> servicios = servicioRepository.findAllByActivoTrue();
        return servicios.stream()
        .map(servicioMapper::toServicioResponseDTO)
        .collect(Collectors.toList());

    }

    @Override
    public List<ServicioResponseDTO> listarTodos(){
        return servicioRepository.findAll()
        .stream()
        .map(servicioMapper::toServicioResponseDTO)
        .collect(Collectors.toList());
    }

    @Override
    public ServicioResponseDTO actualizar(String serviceCode, ServicioCreateDTO dto){
        Servicio servicio = servicioRepository.findByServiceCode(serviceCode)
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado" + serviceCode));
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecios(servicioMapper.toServicio(dto).getPrecios());
        return servicioMapper.toServicioResponseDTO(servicioRepository.save(servicio));
    }

    @Override
    public void desactivar(String serviceCode){
        Servicio servicio = servicioRepository.findByServiceCode(serviceCode)
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado" + serviceCode));
        servicio.setActivo(false);
        servicioRepository.save(servicio);
    }

    @Override
    public void activar(String serviceCode){
        Servicio servicio = servicioRepository.findByServiceCode(serviceCode)
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado" + serviceCode));
        servicio.setActivo(true);
        servicioRepository.save(servicio);
    }

    @Override
    public void eliminar(String serviceCode){
        Servicio servicio = servicioRepository.findByServiceCode(serviceCode)
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado" + serviceCode));
        servicioRepository.delete(servicio);
    }
}