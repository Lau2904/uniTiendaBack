package com.uniTienda.Model.Orden;

import java.time.LocalDate;
import java.time.YearMonth;

import com.uniTienda.Model.Usuario;
import com.uniTienda.dto.TipoTarjeta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "Tarjeta")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_tarjeta")
    @Enumerated(EnumType.STRING)
    private TipoTarjeta tipoTarjeta;

    @Column(name = "numero_tarjeta")
    private String numeroTarjeta; // Últimos 4 dígitos

    
    @Transient // No almacenar directamente YearMonth en la base de datos
    private YearMonth fechaExpiracion;
     @Column(name = "fecha_expiracion") 

    private LocalDate fechaExpiracionDb;


    @Column(name = "nombre_titular")
    private String nombreTitular;


    public void setFechaExpiracion(YearMonth fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
        this.fechaExpiracionDb = fechaExpiracion.atDay(1); // Guardar con el primer día del mes
    }
}
