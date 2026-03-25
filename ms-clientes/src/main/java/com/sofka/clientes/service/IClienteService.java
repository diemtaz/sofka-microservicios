package com.sofka.clientes.service;

import com.sofka.clientes.model.dto.ClientePatchDTO;
import com.sofka.clientes.model.dto.ClienteRequestDTO;
import com.sofka.clientes.model.dto.ClienteResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio. Definir contrato via interfaz es buena práctica:
 * - Facilita el mockeo en pruebas unitarias (Mockito.mock(IClienteService.class))
 * - Permite cambiar la implementación sin modificar el controlador
 * - Sigue el principio de Inversión de Dependencias (SOLID - D)
 */
public interface IClienteService {
    List<ClienteResponseDTO> listarTodos();
    ClienteResponseDTO buscarPorClienteId(String clienteId);
    ClienteResponseDTO crear(ClienteRequestDTO dto);
    ClienteResponseDTO actualizar(String clienteId, ClienteRequestDTO dto);
    ClienteResponseDTO actualizarParcial(String clienteId, ClientePatchDTO dto);
    void eliminar(String clienteId);
}
