package com.sofka.cuentas.repository;

import com.sofka.cuentas.model.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Query("""
        SELECT m FROM Movimiento m
        JOIN FETCH m.cuenta c
        WHERE c.clienteId = :clienteId
          AND m.fecha BETWEEN :fechaInicio AND :fechaFin
        ORDER BY m.fecha DESC
        """)
    List<Movimiento> findByClienteIdAndFechaBetween(
            @Param("clienteId") String clienteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    List<Movimiento> findByCuenta_NumeroCuenta(String numeroCuenta);
}
