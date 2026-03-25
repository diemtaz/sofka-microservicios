package com.sofka.clientes.messaging;

import com.sofka.clientes.model.dto.ClienteEventoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publicador de eventos al broker.
 * - @Slf4j provee logging sin boilerplate
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.cliente-creado}")
    private String rkClienteCreado;

    @Value("${rabbitmq.routing-key.cliente-eliminado}")
    private String rkClienteEliminado;

    public void publicarClienteCreado(ClienteEventoDTO evento) {
        log.info("Publicando evento CLIENTE_CREADO para clienteId: {}", evento.getClienteId());
        rabbitTemplate.convertAndSend(exchange, rkClienteCreado, evento);
    }

    public void publicarClienteEliminado(ClienteEventoDTO evento) {
        log.info("Publicando evento CLIENTE_ELIMINADO para clienteId: {}", evento.getClienteId());
        rabbitTemplate.convertAndSend(exchange, rkClienteEliminado, evento);
    }
}
