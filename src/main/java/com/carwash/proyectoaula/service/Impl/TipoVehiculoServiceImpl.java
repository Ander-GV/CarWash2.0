package com.carwash.proyectoaula.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoCreateDTO;
import com.carwash.proyectoaula.dto.tipoVehiculo.TipoDeVehiculoResponseDTO;
import com.carwash.proyectoaula.mapper.TipoDeVehiculoMapper;
import com.carwash.proyectoaula.model.entity.TipoVehiculo;
import com.carwash.proyectoaula.repository.TipoVehiculoRepository;
import com.carwash.proyectoaula.service.TipoVehiculoService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TipoVehiculoServiceImpl implements TipoVehiculoService{
    
    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final TipoDeVehiculoMapper tipoVehiculoMapper;

    @Override
    public TipoDeVehiculoResponseDTO crear(TipoDeVehiculoCreateDTO dto){
        if(tipoVehiculoRepository.existsByNombre(dto.getNombre())){
            throw new RuntimeException("El tipo de vehiculo ya existe");
        }
        TipoVehiculo tipoVehiculo = tipoVehiculoMapper.toTipo(dto);
        return tipoVehiculoMapper.toTipoResponse(tipoVehiculoRepository.save(tipoVehiculo));
    }

    @Override
    public List<TipoDeVehiculoResponseDTO> listarActivos() {
        return tipoVehiculoRepository.findAllByActivoTrue()
        .stream()
        .map(tipoVehiculoMapper::toTipoResponse)
        .toList();
    }

    @Override
    public List<TipoDeVehiculoResponseDTO> listarTodos() {
        return tipoVehiculoRepository.findAll()
        .stream()
        .map(tipoVehiculoMapper::toTipoResponse)
        .toList();
    }

    @Override
    public void desactivar(String id) {
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Tipo de vehiculo no encontrado"));
        tipoVehiculo.setActivo(false);
        tipoVehiculoRepository.save(tipoVehiculo);
    }

    @Override
    public void activar(String id) {
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Tipo de vehiculo no encontrado"));
        tipoVehiculo.setActivo(true);
        tipoVehiculoRepository.save(tipoVehiculo);
    }

    @Override
    public void eliminar(String id) {
        tipoVehiculoRepository.deleteById(id);
    }
}
