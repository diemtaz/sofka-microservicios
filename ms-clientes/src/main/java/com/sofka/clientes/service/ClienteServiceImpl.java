package com.sofka.clientes.service;

import com.sofka.clientes.exception.RecursoDuplicadoException;
import com.sofka.clientes.exception.RecursoNoEncontradoException;
import com.sofka.clientes.messaging.ClienteEventProducer;
import com.sofka.clientes.model.dto.ClienteEventoDTO;
import com.sofka.clientes.model.dto.ClientePatchDTO;
import com.sofka.clientes.model.dto.ClienteRequestDTO;
import com.sofka.clientes.model.dto.ClienteResponseDTO;
import com.sofka.clientes.model.entity.Cliente;
import com.sofka.clientes.model.mapper.ClienteMapper;
import com.sofka.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Implementación de la lógica de negocio para Clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteEventProducer eventProducer;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarTodos(Pageable pageable) {
        log.debug("Listando todos los clientes");
        return clienteRepository.findAll(pageable)
                .map(clienteMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorClienteId(String clienteId) {
        log.debug("Buscando cliente con clienteId: {}", clienteId);
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));
        return clienteMapper.toDTO(cliente);
    }

    @Override
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {
        log.info("Creando nuevo cliente con clienteId: {}", dto.getClienteId());

        if (clienteRepository.existsByClienteId(dto.getClienteId())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un cliente con clienteId: " + dto.getClienteId());
        }
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new RecursoDuplicadoException(
                    "Ya existe una persona con identificación: " + dto.getIdentificacion());
        }

        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente guardado = clienteRepository.save(cliente);

        // Publicar evento asíncrono al broker para que MS-Cuentas se entere
        eventProducer.publicarClienteCreado(ClienteEventoDTO.builder()
                .clienteId(guardado.getClienteId())
                .nombre(guardado.getNombre())
                .tipoEvento("CREADO")
                .build());

        log.info("Cliente creado exitosamente con id: {}", guardado.getId());
        return clienteMapper.toDTO(guardado);
    }

    @Override
    public ClienteResponseDTO actualizar(String clienteId, ClienteRequestDTO dto) {
        log.info("Actualizando cliente con clienteId: {}", clienteId);

        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));

        clienteMapper.updateEntityFromDTO(dto, cliente);
        Cliente actualizado = clienteRepository.save(cliente);

        log.info("Cliente actualizado exitosamente: {}", clienteId);
        return clienteMapper.toDTO(actualizado);
    }

    @Override
    public ClienteResponseDTO actualizarParcial(String clienteId, ClientePatchDTO dto) {
        log.info("Actualizacion parcial de cliente: {}", clienteId);
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));

        if (dto.getNombre()    != null) cliente.setNombre(dto.getNombre());
        if (dto.getGenero()    != null) cliente.setGenero(dto.getGenero());
        if (dto.getEdad()      != null) cliente.setEdad(dto.getEdad());
        if (dto.getDireccion() != null) cliente.setDireccion(dto.getDireccion());
        if (dto.getTelefono()  != null) cliente.setTelefono(dto.getTelefono());
        if (dto.getContrasena()!= null) cliente.setContrasena(dto.getContrasena());
        if (dto.getEstado()    != null) cliente.setEstado(dto.getEstado());

        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Override
    public void eliminar(String clienteId) {
        log.info("Eliminando cliente con clienteId: {}", clienteId);

        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));

        clienteRepository.delete(cliente);

        // Notificar a MS-Cuentas para que maneje la eliminación del cliente
        eventProducer.publicarClienteEliminado(ClienteEventoDTO.builder()
                .clienteId(clienteId)
                .nombre(cliente.getNombre())
                .tipoEvento("ELIMINADO")
                .build());

        log.info("Cliente eliminado exitosamente: {}", clienteId);
    }
}
