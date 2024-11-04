package com.uniTienda.Model.Orden;

import java.math.BigDecimal;
import java.util.Date;

import com.uniTienda.Model.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "GanaGana")
public class GanaGana {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(name = "codigo_factura", unique = true, nullable = false)
    private String codigoFactura; // Número único de referencia de la factura

    @Column(name = "monto", nullable = false)
    private BigDecimal monto; // Monto total de la factura

    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE"; // Estado inicial de la factura

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false)
    private Date fechaCreacion = new Date(); // Fecha de creación de la factura

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_limite_pago", nullable = false)
    private Date fechaLimitePago; // Fecha límite para realizar el pago

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = true)
    private Orden orden; // Relación opcional con la orden

    // Getters y Setters
}
