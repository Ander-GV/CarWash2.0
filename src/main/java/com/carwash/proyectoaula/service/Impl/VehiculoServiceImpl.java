package com.carwash.proyectoaula.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carwash.proyectoaula.dto.vehiculo.VehiculoCreateDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoResponseDTO;
import com.carwash.proyectoaula.dto.vehiculo.VehiculoUpdateDTO;
import com.carwash.proyectoaula.mapper.VehiculoMapper;
import com.carwash.proyectoaula.model.entity.Persona;
import com.carwash.proyectoaula.model.entity.Vehiculo;
import com.carwash.proyectoaula.repository.PersonaRepository;
import com.carwash.proyectoaula.repository.TipoVehiculoRepository;
import com.carwash.proyectoaula.repository.VehiculoRepository;
import com.carwash.proyectoaula.service.VehiculoService;

// Implementación de la lógica para el registro, edición y búsqueda de vehículos de los clientes
@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    @Autowired
    private VehiculoMapper vehiculoMapper;

    // Trae únicamente los vehículos activos que le pertenecen a un cliente específico
    @Override
    public List<VehiculoResponseDTO> listarVehiculos(String userCode){
        Persona cliente = personaRepository.findByUserCode(userCode)
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return vehiculoRepository.findByClienteId(cliente.getId())
        .stream()
        .filter(vehiculo -> Boolean.TRUE.equals(vehiculo.getActivo()))
        .map(vehiculoMapper::toVehiculoResponseDTO)
        .collect(Collectors.toList());
    }

    // Busca un vehículo puntualmente por su placa
    @Override
    public VehiculoResponseDTO buscarVehiculo(String placa){
        return vehiculoRepository.findByPlaca(placa)
        .map(vehiculoMapper::toVehiculoResponseDTO)
        .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado"));
    }

    // Registra un nuevo vehículo verificando que la placa no esté duplicada
    @Override
    public VehiculoResponseDTO crearVehiculo(VehiculoCreateDTO dto){
        Persona cliente = personaRepository.findByDocumento(dto.getDocumentoCliente())
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        if(vehiculoRepository.existsByPlaca(dto.getPlaca())){
            throw new RuntimeException("Vehiculo ya existe");
        }
        Vehiculo vehiculo = vehiculoMapper.toVehiculo(dto);
        
        com.carwash.proyectoaula.model.entity.TipoVehiculo tipoCompleto = tipoVehiculoRepository.findById(dto.getTipoVehiculo().getId())
                .orElseThrow(() -> new RuntimeException("Tipo de vehiculo no encontrado"));
        vehiculo.setTipoVehiculo(tipoCompleto);
        
        vehiculo.setClienteId(cliente.getId());
        vehiculo.setActivo(true);
        return vehiculoMapper.toVehiculoResponseDTO(vehiculoRepository.save(vehiculo));
    }


    // Actualiza la pintura, modelo, año u otra característica del carro
    @Override
    public VehiculoResponseDTO actualizarVehiculo(String placa, VehiculoUpdateDTO dto){
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
        .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado"));
        vehiculo.setColor(dto.getColor());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setYear(dto.getYear());
        vehiculo.setMarca(dto.getMarca());
        
        com.carwash.proyectoaula.model.entity.TipoVehiculo tipoCompleto = tipoVehiculoRepository.findById(dto.getTipoVehiculo().getId())
                .orElseThrow(() -> new RuntimeException("Tipo de vehiculo no encontrado"));
        vehiculo.setTipoVehiculo(tipoCompleto);
        
        vehiculo.setActivo(dto.getActivo());
        return vehiculoMapper.toVehiculoResponseDTO(vehiculoRepository.save(vehiculo));
    }

    
    // Eliminación lógica del vehículo (solo se oculta cambiando activo a false)
    @Override
    public void eliminarVehiculo(String placa){
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
        .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado"));
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }

}
