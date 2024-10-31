package com.uniTienda.Model.Productos;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

  
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock;

    private String tamaño;

    @Column(precision = 5, scale = 2)
    private BigDecimal descuento;

    
    @Column(name = "colores")
    private List<String> colores; 

    private List<String> imagenes;  // Lista de rutas de imágenes

    private String categoria;
}