package com.example.pedidos360_pagos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody Map<String, Object> datosPago) {
        Map<String, Object> respuesta = new HashMap<>();
        
        // 1. Validar presencia de campos requeridos
        if (!datosPago.containsKey("monto") || !datosPago.containsKey("numeroTarjeta")) {
            respuesta.put("estado", "ERROR");
            respuesta.put("mensaje", "Datos de pago incompletos (monto y numeroTarjeta son obligatorios)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }

        Double monto = Double.valueOf(datosPago.get("monto").toString());
        String numeroTarjeta = datosPago.get("numeroTarjeta").toString();

        // 2. Validación de monto
        if (monto <= 0) {
            respuesta.put("estado", "RECHAZADO");
            respuesta.put("mensaje", "El monto del pago debe ser mayor a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }

        // 3. Reglas de simulación de rechazo bancario
        if (numeroTarjeta.endsWith("0000")) {
            // Regla de prueba: Tarjetas terminadas en '0000' simulan fondos insuficientes
            respuesta.put("idTransaccion", UUID.randomUUID().toString().substring(0, 8));
            respuesta.put("estado", "RECHAZADO");
            respuesta.put("motivo", "FONDOS_INSUFICIENTES");
            respuesta.put("mensaje", "Transacción rechazada por el banco emisor");
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(respuesta);
        } else if (numeroTarjeta.endsWith("9999")) {
            // Regla de prueba: Tarjetas terminadas en '9999' simulan tarjeta bloqueada/robada
            respuesta.put("idTransaccion", UUID.randomUUID().toString().substring(0, 8));
            respuesta.put("estado", "RECHAZADO");
            respuesta.put("motivo", "TARJETA_BLOQUEADA");
            respuesta.put("mensaje", "Tarjeta bloqueada o no autorizada para compras en línea");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuesta);
        }

        // 4. Pago Aprobado
        respuesta.put("idTransaccion", UUID.randomUUID().toString().substring(0, 8));
        respuesta.put("estado", "APROBADO");
        respuesta.put("montoProcesado", monto);
        respuesta.put("mensaje", "Pago procesado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> consultarEstado(@PathVariable String id) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("idTransaccion", id);
        respuesta.put("estado", "APROBADO");
        return ResponseEntity.ok(respuesta);
    }
}