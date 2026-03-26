package com.sofka.cuentas.service;

import com.sofka.cuentas.model.dto.MovimientoPatchDTO;
import com.sofka.cuentas.model.dto.MovimientoRequestDTO;
import com.sofka.cuentas.model.dto.MovimientoResponseDTO;
import com.sofka.cuentas.model.dto.ReporteMovimientoDTO;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMovimientoService {
    Page<MovimientoResponseDTO> listarTodos(Pageable pageable);
    MovimientoResponseDTO buscarPorId(Long id);
    MovimientoResponseDTO registrar(MovimientoRequestDTO dto);
    MovimientoResponseDTO actualizar(Long id, MovimientoRequestDTO dto);
    MovimientoResponseDTO actualizarParcial(Long id, MovimientoPatchDTO dto);
    void eliminar(Long id);

    // F4 - Reporte
    List<ReporteMovimientoDTO> generarReporte(
            String clienteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);
}
