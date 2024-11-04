package com.uniTienda.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.uniTienda.Model.Orden.Orden;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal monto;

    private LocalDateTime fecha;

    private String metodoPago; // Ej. TARJETA, GANA_GANA, PSE, RESERVA

    private String estado; // Ej. PENDIENTE, COMPLETADO

    @ManyToOne
    @JoinColumn(name = "orden_id")
    private Orden orden;
}