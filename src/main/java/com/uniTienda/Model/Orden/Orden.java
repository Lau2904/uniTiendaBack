package com.uniTienda.Model.Orden;



import java.math.BigDecimal;
import java.util.Date;

import com.uniTienda.Model.Direccion;
import com.uniTienda.Model.Usuario;
import com.uniTienda.dto.EstadoOrden;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "Orden")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    private String metodoPago;   // Ej., TARJETA, PSE, GANA_GANA, RESERVA

    @OneToOne
    @JoinColumn(name = "tarjeta_id")
    private Tarjeta tarjeta;

    @OneToOne
    @JoinColumn(name = "pse_id")
    private PSE pse;

    @OneToOne
    @JoinColumn(name = "gana_gana_id")
    private GanaGana ganaGana;

    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;
    private EstadoOrden estado; 

    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;  // Campo único para el número de pedido

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @Temporal(TemporalType.DATE)
    private Date fechaMaximaEntrega; // Fecha límite para entregas

    @Temporal(TemporalType.DATE)
    private Date fechaMaximaReclamo; // Fecha límite para reclamo en tienda (reservas)


    // Getters y Setters
}
