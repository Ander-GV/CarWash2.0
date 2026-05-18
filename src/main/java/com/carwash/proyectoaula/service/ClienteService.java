package com.carwash.proyectoaula.service;

import java.util.List;

import com.carwash.proyectoaula.dto.cliente.ClienteCreateDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteResponseDTO;
import com.carwash.proyectoaula.dto.cliente.ClienteUpdateDTO;

public interface ClienteService {

    List<ClienteResponseDTO> listarClientes();
    ClienteResponseDTO buscarCliente(String userCode);
    ClienteResponseDTO crearCliente(ClienteCreateDTO dto);
    ClienteResponseDTO actualizarCliente(String userCode, ClienteUpdateDTO dto);
    ClienteResponseDTO buscarDocumento(String documento);
    void eliminarCliente(String userCode);
}
