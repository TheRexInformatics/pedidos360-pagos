package com.example.pedidos360_pagos;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PagoConsumer {

    @RabbitListener(queues = RabbitMQConfig.COLA_PAGOS)
    public void recibirPago(Map<String, Object> mensaje) {
        System.out.println("<<< [RABBITMQ PAGOS] Pago recibido: " + mensaje);
        System.out.println("<<< Registrando pago de $" + mensaje.get("total") + " para el pedido #" + mensaje.get("idPedido") + " con estado: " + mensaje.get("estado"));
    }
}