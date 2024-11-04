package com.uniTienda.Controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniTienda.Model.Pago;
import com.uniTienda.Service.PagoService;


@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // Endpoint para crear un nuevo pago
    @PostMapping("/crear")
    public ResponseEntity<Pago> crearPago(
            @RequestParam Long ordenId,
            @RequestParam BigDecimal monto,
            @RequestParam String metodoPago) {
        Pago nuevoPago = pagoService.crearPago(ordenId, monto, metodoPago);
        return ResponseEntity.ok(nuevoPago);
    }

    // Endpoint para actualizar el estado de un pago
    @PutMapping("/{pagoId}/actualizarEstado")
    public ResponseEntity<Pago> actualizarEstadoPago(
            @PathVariable Long pagoId,
            @RequestParam String nuevoEstado) {
        Pago pagoActualizado = pagoService.actualizarEstadoPago(pagoId, nuevoEstado);
        return ResponseEntity.ok(pagoActualizado);
    }
}